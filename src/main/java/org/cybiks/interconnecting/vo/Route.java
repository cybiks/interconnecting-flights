package org.cybiks.interconnecting.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {

    private String airportFrom;
    private String airportTo;
    private String connectingAiport;
    private boolean newRoute;
    private boolean seasonalRoute;
    private String operator;
    private String group;


    public Route() {
    }

    public String getAirportFrom() {
        return airportFrom;
    }

    public void setAirportFrom(String airportFrom) {
        this.airportFrom = airportFrom;
    }

    public String getAirportTo() {
        return airportTo;
    }

    public void setAirportTo(String airportTo) {
        this.airportTo = airportTo;
    }

    public String getConnectingAiport() {
        return connectingAiport;
    }

    public void setConnectingAiport(String connectingAiport) {
        this.connectingAiport = connectingAiport;
    }

    public boolean isNewRoute() {
        return newRoute;
    }

    public void setNewRoute(boolean newRoute) {
        this.newRoute = newRoute;
    }

    public boolean isSeasonalRoute() {
        return seasonalRoute;
    }

    public void setSeasonalRoute(boolean seasonalRoute) {
        this.seasonalRoute = seasonalRoute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Route{" +
                "airportFrom='" + airportFrom + '\'' +
                ", airportTo='" + airportTo + '\'' +
                ", connectingAiport='" + connectingAiport + '\'' +
                ", newRoute=" + newRoute +
                ", seasonalRoute=" + seasonalRoute +
                ", operator='" + operator + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
