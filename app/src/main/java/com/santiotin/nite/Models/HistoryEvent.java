package com.santiotin.nite.Models;


public class HistoryEvent {

    private String eventId;
    private String eventTitle;
    private String eventClub;
    private int day;
    private int month;
    private int year;

    public HistoryEvent(){

    }

    public HistoryEvent(String eventId, String eventTitle, String eventClub, int day, int month, int year){
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventClub = eventClub;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventClub() {
        return eventClub;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
