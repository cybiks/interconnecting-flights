package org.cybiks.interconnecting.controller;

import org.cybiks.interconnecting.service.FlightService;
import org.cybiks.interconnecting.timezone.TimeZoneIATAMapper;
import org.cybiks.interconnecting.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private FlightService flightService;

    private TimeZoneIATAMapper timeZoneIATAMapper;

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }

    @Autowired
    public void setTimeZoneIATAMapper(TimeZoneIATAMapper timeZoneIATAMapper) {
        this.timeZoneIATAMapper = timeZoneIATAMapper;
    }

    //for example: http://localhost:8080/somevalidcontext/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00
    //for example: http://localhost:8080/greeting/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00
    @RequestMapping("/greeting/interconnections")
    public List<Result> greeting(@RequestParam(value = "departure") String departure,
                                 @RequestParam(value = "arrival") String arrival,
                                 @RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                 @RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime
    ) {


        //direct flights
        Schedule schedule = flightService.getSchedule(departure, arrival, departureDateTime.getYear(), departureDateTime.getMonthValue());
        log.info(schedule.toString());
        List<Result> results = new LinkedList<>();
        List<Leg> legs = getLegs(departure, arrival, departureDateTime, arrivalDateTime, schedule);

        results.add(new Result(0, legs));
        //assume 1 month
        //scheduleDays
        // Interconnection option
        List<String> intermediate = flightService.getIntermediateAirports(departure, arrival);

        //in direct flights
        for (String intermediateAirport : intermediate) {
            Schedule scheduleFirstFlight = flightService.getSchedule(departure, intermediateAirport, departureDateTime.getYear(), departureDateTime.getMonthValue());
            Schedule scheduleSecondFlight = flightService.getSchedule(intermediateAirport, arrival, departureDateTime.getYear(), departureDateTime.getMonthValue());
            if (scheduleFirstFlight == null || scheduleSecondFlight == null) {
                continue;
            }

            List<Leg> getLegsFirstFlight = getLegs(departure, intermediateAirport, departureDateTime, arrivalDateTime, scheduleFirstFlight);
            for (Leg firstFlight : getLegsFirstFlight) {
                List<Leg> getLegsSecondFlight = getLegs(intermediateAirport, arrival, firstFlight.getArrivalDateTime().plusHours(2), arrivalDateTime, scheduleSecondFlight);
                if (getLegsSecondFlight.size() > 0) {
                    results.add(new Result(1, Stream.concat(Stream.of(firstFlight), getLegsSecondFlight.stream()).collect(Collectors.toList())));
                }
            }

        }

        return results;
    }

    private List<Leg> getLegs(String departure,
                              String arrival,
                              LocalDateTime departureDateTime,
                              LocalDateTime arrivalDateTime,
                              Schedule schedule) {
        ZonedDateTime departureTotalDateTime = ZonedDateTime.of(departureDateTime, ZoneId.of("UTC"));
        ZonedDateTime arrivalTotalDateTime = ZonedDateTime.of(arrivalDateTime, ZoneId.of("UTC"));

        List<Leg> legs = new LinkedList<>();
        for (ScheduleDay scheduleDay : schedule.getDays()) {
            if ((scheduleDay.getDay() >= (departureDateTime.getDayOfMonth())) && (scheduleDay.getDay() <= (arrivalDateTime.getDayOfMonth()))) {
                List<Flight> scheduleFlights = scheduleDay.getFlights();
                for (Flight flight : scheduleFlights) {
                    ZonedDateTime departureZonedDateTime = getDateTime(departure, departureDateTime.getYear(), departureDateTime.getMonthValue(), departureDateTime.getDayOfMonth(), flight.getDepartureTime(), false);
                    ZonedDateTime arrivalZonedDateTime = getDateTime(arrival, arrivalDateTime.getYear(), arrivalDateTime.getMonthValue(), arrivalDateTime.getDayOfMonth(), flight.getArrivalTime(), true);
                    if (departureTotalDateTime.isBefore(departureZonedDateTime) && arrivalTotalDateTime.isAfter(arrivalZonedDateTime)) {
                        legs.add(new Leg(departure, arrival, departureZonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime(), arrivalZonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()));
                    }
                }
            }
        }
        return legs;
    }

    private ZonedDateTime getDateTime(String aiport, int year, int month, int dayOfMonth, String airportTime, boolean isArrival) {
        LocalDate ld = LocalDate.of(year, month, dayOfMonth);
        LocalTime lt = LocalTime.parse(airportTime);
        if (isArrival && lt.getHour() == 0) {
            ld = ld.plusDays(1);
        }
        LocalDateTime ldt = LocalDateTime.of(ld, lt);

        String timeZoneID = timeZoneIATAMapper.findTimeZoneID(aiport);
        log.info("TimeZone for airport {} is {} ", aiport, timeZoneID);
        return ZonedDateTime.of(ldt, ZoneId.of(timeZoneID));
    }
}
