package org.cybiks.interconnecting.service;


import org.cybiks.interconnecting.vo.FlightResult;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    /**
     * Find all direct flights if available.
     *
     * @param departureAirport  Departure Airport.
     * @param arrivalAirport    Arrival Airport.
     * @param departureDateTime Departure date and time. Type LocalDateTime UTC.
     * @param arrivalDateTime   Arrival date and time. Type LocalDateTime UTC.
     * @return all direct flights if available.
     */
    List<FlightResult> getDirectFlights(String departureAirport, String arrivalAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);

    /**
     * Find all interconnected flights with a maximum of one stop if available (for example: DUB - STN - WRO)
     *
     * @param departureAirport    Departure airport.
     * @param arrivalAirport      Arrival airport.
     * @param intermediateAirport Intermediate airport.
     * @param departureDateTime   Departure date and time. Type LocalDateTime UTC.
     * @param arrivalDateTime     Arrival date and time. Type LocalDateTime UTC.
     * @return all interconnected flights with a maximum of one stop if available
     */
    List<FlightResult> getIntermediateFlight(String departureAirport, String arrivalAirport, String intermediateAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);

    /**
     * Find list of intermediate airports.
     *
     * @param departureAirport Departure airport.
     * @param arrivalAirport   Arrival airport.
     * @return Intermediate airports list. Type List<String>.
     */
    List<String> getIntermediateAirports(String departureAirport, String arrivalAirport);

}
