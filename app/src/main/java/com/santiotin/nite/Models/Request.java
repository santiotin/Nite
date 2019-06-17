package com.santiotin.nite.Models;


public class Request {

    private String personId;
    private String personName;
    private int day;
    private int month;
    private int year;

    public Request(){

    }

    public Request(String personId, String personName, int day, int month, int year){
        this.personId = personId;
        this.personName = personName;
        this.day = day;
        this.month = month;
        this.year = year;
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
}
