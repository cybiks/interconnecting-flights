package org.cybiks.interconnecting.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleDay {
    private Integer day;
    private List<Flight> flights;

    public ScheduleDay() {
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "ScheduleDay{" +
                "day=" + day +
                ", flights=" + flights +
                '}';
    }
}
