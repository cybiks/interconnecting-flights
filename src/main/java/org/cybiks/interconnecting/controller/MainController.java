package org.cybiks.interconnecting.controller;

import org.cybiks.interconnecting.service.FlightService;
import org.cybiks.interconnecting.vo.FlightResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


@RestController
public class MainController {

    private FlightService flightService;

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }

    @RequestMapping("/interconnections")
    public List<FlightResult> greeting(@RequestParam(value = "departure") String departure,
                                       @RequestParam(value = "arrival") String arrival,
                                       @RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                       @RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {

        List<FlightResult> flightResults = new LinkedList<>(flightService.getDirectFlights(departure, arrival, departureDateTime, arrivalDateTime));

        List<String> intermediate = flightService.getIntermediateAirports(departure, arrival);

        for (String intermediateAirport : intermediate) {
            flightResults.addAll(flightService.getIntermediateFlight(departure, arrival, intermediateAirport, departureDateTime, arrivalDateTime));
        }
        return flightResults;
    }


}
