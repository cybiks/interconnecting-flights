package org.cybiks.interconnecting.service;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.cybiks.interconnecting.vo.Route;
import org.cybiks.interconnecting.vo.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {
    private static final Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Route> getRouteList() {
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

    @Override
    public List<String> getIntermediateAirports(String departureAirport, String arrivalAirport) {
        List<Route> routes = getRouteList();
        MultiMap<String, String> airportsConnectionsAB = new MultiValueMap<>();
        MultiMap<String, String> airportsConnectionsBA = new MultiValueMap<>();
        for (Route route : routes
        ) {
            if (route.getConnectingAiport() == null && "RYANAIR".equals(route.getOperator()))
                airportsConnectionsAB.put(route.getAirportFrom(), route.getAirportTo());
            airportsConnectionsBA.put(route.getAirportTo(), route.getAirportFrom());
        }
        log.info("test!!!!");
        log.info(airportsConnectionsAB.toString());
        log.info(airportsConnectionsBA.toString());

        List<String> from = (List<String>) airportsConnectionsAB.get(departureAirport);

        List<String> to = (List<String>) airportsConnectionsBA.get(arrivalAirport);
        log.info("From {}: {}", departureAirport, from);
        log.info("To {}: {} ", arrivalAirport, to);
        from.retainAll(to);
        log.info("intersections : " + from);
        return from;
    }

    @Override
    public Schedule getSchedule(String departureAirport, String arrivalAirport, int year, int month) {
        //https://services-api.ryanair.com/timtbl/3/schedules/DUB/WRO/years/2019/months/6
        Schedule schedule;
        String request = String.format("https://services-api.ryanair.com/timtbl/3/schedules/%s/%s/years/%s/months/%s", departureAirport, arrivalAirport, year, month);
        try {
            log.info("request : " + request);
            schedule = restTemplate.getForObject(request, Schedule.class);
            log.info("schedule : " + schedule);
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            log.error("Request: {} return HttpClientErrorException", request);
            schedule = null;
        }
        return schedule;
    }
}
