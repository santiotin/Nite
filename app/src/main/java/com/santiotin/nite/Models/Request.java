package com.santiotin.nite.Models;


public class Request {

    private String requestId;
    private String personId;
    private String personName;
    private int day;
    private int month;
    private int year;

    public Request(){

    }

    public Request(String requestId, String personId, String personName, int day, int month, int year){
        this.requestId = requestId;
        this.personId = personId;
        this.personName = personName;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getPersonId() {
        return personId;
    }

    public String getRequestId() {
        return requestId;
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
}
