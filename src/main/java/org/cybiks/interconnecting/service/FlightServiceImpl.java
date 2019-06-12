package org.cybiks.interconnecting.service;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.cybiks.interconnecting.timezone.TimeZoneIATAMapper;
import org.cybiks.interconnecting.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightServiceImpl implements FlightService {
    private static final Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);
    private RestTemplate restTemplate;
    private TimeZoneIATAMapper timeZoneIATAMapper;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setTimeZoneIATAMapper(TimeZoneIATAMapper timeZoneIATAMapper) {
        this.timeZoneIATAMapper = timeZoneIATAMapper;
    }

    @Override
    public List<FlightResult> getDirectFlights(String departureAirport, String arrivalAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        List<FlightResult> flightResults = new LinkedList<>();
        LocalDateTime dateTimeCounter = departureDateTime;
        while (dateTimeCounter.isBefore(arrivalDateTime)) {
            Optional<Schedule> schedule = getSchedule(departureAirport, arrivalAirport, dateTimeCounter.getYear(), dateTimeCounter.getMonthValue());
            schedule.ifPresent(s -> flightResults.add(new FlightResult(0, getLegs(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime, Collections.singletonList(s)))));
            dateTimeCounter = dateTimeCounter.plusMonths(1);
        }
        return flightResults;
    }

    @Override
    public List<FlightResult> getIntermediateFlight(String departureAirport, String arrivalAirport, String intermediateAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        List<FlightResult> flightResults = new LinkedList<>();
        List<Schedule> schedulesFirstFlight = new LinkedList<>();
        List<Schedule> schedulesSecondFlight = new LinkedList<>();
        LocalDateTime dateTimeCounter = departureDateTime;
        while (dateTimeCounter.isBefore(arrivalDateTime)) {
            Optional<Schedule> scheduleFirst = getSchedule(departureAirport, intermediateAirport, dateTimeCounter.getYear(), dateTimeCounter.getMonthValue());
            scheduleFirst.ifPresent(schedulesFirstFlight::add);
            Optional<Schedule> scheduleSecond = getSchedule(intermediateAirport, arrivalAirport, dateTimeCounter.getYear(), dateTimeCounter.getMonthValue());
            scheduleSecond.ifPresent(schedulesSecondFlight::add);
            dateTimeCounter = dateTimeCounter.plusMonths(1);
        }
        if (schedulesFirstFlight.size() == 0 || schedulesSecondFlight.size() == 0) {
            return flightResults;
        }

        List<Leg> getLegsFirstFlight = getLegs(departureAirport, intermediateAirport, departureDateTime, arrivalDateTime, schedulesFirstFlight);
        for (Leg firstFlight : getLegsFirstFlight) {
            //For interconnected flights the difference between the arrival and the next departure
            //should be 2h or greater
            //The list should be of the following
            LocalDateTime secondFlightDepartureDateTime = firstFlight.getArrivalDateTime().plusHours(2);
            List<Leg> legsSecondFlight = getLegs(intermediateAirport, arrivalAirport, secondFlightDepartureDateTime, arrivalDateTime, schedulesSecondFlight);
            if (legsSecondFlight.size() > 0) {
                flightResults.add(new FlightResult(1, Stream.concat(Stream.of(firstFlight), legsSecondFlight.stream()).collect(Collectors.toList())));
            }
        }
        return flightResults;
    }

    @Override
    public List<String> getIntermediateAirports(String departureAirport, String arrivalAirport) {
        List<Route> routes = getRouteList();
        MultiMap<String, String> airportsConnectionsAB = new MultiValueMap<>();
        MultiMap<String, String> airportsConnectionsBA = new MultiValueMap<>();
        for (Route route : routes
        ) {
            //Routes API: https://services-api.ryanair.com/locate/3/routes which returns a list of all
            //available routes based on the airport's IATA codes. Please note that only routes with:
            //connectingAirport set to null and operator set to RYANAIR should be used.
            if (route.getConnectingAiport() == null && "RYANAIR".equals(route.getOperator())) {
                airportsConnectionsAB.put(route.getAirportFrom(), route.getAirportTo());
                airportsConnectionsBA.put(route.getAirportTo(), route.getAirportFrom());
            }
        }

        List<String> from = (List<String>) airportsConnectionsAB.get(departureAirport);
        List<String> to = (List<String>) airportsConnectionsBA.get(arrivalAirport);
        from.retainAll(to);
        return from;
    }

    private List<Leg> getLegs(String departure,
                              String arrival,
                              LocalDateTime departureDateTime,
                              LocalDateTime arrivalDateTime,
                              List<Schedule> schedules) {
        ZoneId utc = ZoneId.of("UTC");
        ZonedDateTime departureTotalDateTime = ZonedDateTime.of(departureDateTime, utc);
        ZonedDateTime arrivalTotalDateTime = ZonedDateTime.of(arrivalDateTime, utc);

        List<Leg> legs = new LinkedList<>();
        for (Schedule schedule : schedules) {
            for (ScheduleDay scheduleDay : schedule.getDays()) {
                if ((scheduleDay.getDay() >= (departureDateTime.getDayOfMonth())) && (scheduleDay.getDay() <= (arrivalDateTime.getDayOfMonth()))) {
                    List<Flight> scheduleFlights = scheduleDay.getFlights();
                    for (Flight flight : scheduleFlights) {
                        ZonedDateTime departureZonedDateTime = getDateTime(departure, departureDateTime.getYear(), schedule.getMonth(), scheduleDay.getDay(), flight.getDepartureTime(), false);
                        ZonedDateTime arrivalZonedDateTime = getDateTime(arrival, arrivalDateTime.getYear(), schedule.getMonth(), scheduleDay.getDay(), flight.getArrivalTime(), true);
                        if (departureTotalDateTime.isBefore(departureZonedDateTime) && arrivalTotalDateTime.isAfter(arrivalZonedDateTime)) {
                            legs.add(new Leg(departure, arrival, departureZonedDateTime.withZoneSameInstant(utc).toLocalDateTime(), arrivalZonedDateTime.withZoneSameInstant(utc).toLocalDateTime()));
                        }
                    }
                }
            }
        }
        return legs;
    }

    private ZonedDateTime getDateTime(String aiport, int year, int month, int dayOfMonth, String airportTime, boolean isArrival) {
        LocalDate ld = LocalDate.of(year, month, dayOfMonth);
        LocalTime lt = LocalTime.parse(airportTime);
        LocalDateTime ldt = LocalDateTime.of(ld, lt);
        //can be start of new day
        if (isArrival && lt.getHour() == 0) {
            ldt = ldt.plusDays(1);
        }
        String timeZoneID = timeZoneIATAMapper.findTimeZoneID(aiport);
        return ZonedDateTime.of(ldt, ZoneId.of(timeZoneID));
    }


    private List<Route> getRouteList() {
        ResponseEntity<List<Route>> rateResponse =
                restTemplate.exchange("https://services-api.ryanair.com/locate/3/routes",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Route>>() {
                        });
        List<Route> routes = rateResponse.getBody();

        if (routes != null) {
            log.info(routes.toString());
        }
        return routes;
    }

    private Optional<Schedule> getSchedule(String departureAirport, String arrivalAirport, int year, int month) {
        Optional<Schedule> schedule;
        String request = String.format("https://services-api.ryanair.com/timtbl/3/schedules/%s/%s/years/%s/months/%s", departureAirport, arrivalAirport, year, month);
        try {
            schedule = Optional.ofNullable(restTemplate.getForObject(request, Schedule.class));
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            log.error("Request: {} return HttpClientErrorException", request);
            schedule = Optional.empty();
        }
        return schedule;
    }
}
