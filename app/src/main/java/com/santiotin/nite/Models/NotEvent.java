package com.santiotin.nite.Models;


public class NotEvent {

    private String personId;
    private String personName;
    private String eventId;
    private String eventTitle;
    private String eventClub;
    private int day;
    private int month;
    private int year;

    public NotEvent(){

    }

    public NotEvent(String personId, String personName, int day, int month, int year, String eventId, String eventTitle, String eventClub){
        this.personId = personId;
        this.personName = personName;
        this.day = day;
        this.month = month;
        this.year = year;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventClub = eventClub;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPersonName(){
        return personName;
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

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventClub() {
        return eventClub;
    }
}
