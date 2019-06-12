package org.cybiks.interconnecting.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {
    private Integer month;
    private List<ScheduleDay> days;

    public Schedule() {
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<ScheduleDay> getDays() {
        return days;
    }

    public void setDays(List<ScheduleDay> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "month=" + month +
                ", days=" + days +
                '}';
    }
}
