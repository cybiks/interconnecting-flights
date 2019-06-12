package org.cybiks.interconnecting.service;


import org.cybiks.interconnecting.vo.Route;
import org.cybiks.interconnecting.vo.Schedule;

import java.util.List;

public interface FlightService {
    List<Route> getRouteList();

    List<String> getIntermediateAirports(String departureAirport, String arrivalAirport);

    Schedule getSchedule(String departureAirport, String arrivalAirport, int year, int month);
}
