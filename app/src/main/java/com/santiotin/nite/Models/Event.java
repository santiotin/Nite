package com.santiotin.nite.Models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String id;
    private String name;
    private String club;
    private String city;
    private String address;
    private String description;
    private String dress;
    private String music;
    private String age;
    private String startHour;
    private String endHour;

    private String listsDescr;
    private String ticketsDescr;
    private String vipsDescr;

    private int listsPrice;
    private int ticketsPrice;
    private int vipsPrice;

    private int day;
    private int month;
    private int year;
    private int numAssistants;
    private double lati;
    private double longi;

    private Boolean blists;
    private Boolean btickets;
    private Boolean bvips;

    private String webPay;


    public Event(){

    }

    public static class ChildClass implements Serializable {

        public ChildClass() {}
    }

    public Event(String id, String title, String comp, String addr, String descr,
                 int day, int month, int year, String starth, String endh, int numA,
                 String dress, String age, String music, Boolean blists, Boolean btickets, Boolean bvips,
                 String listsDescr, String ticketsDescr, String vipsDescr, int listsPrice, int ticketsPrice, int vipsPrice,
                 double lati, double longi, String city, String webPay) {
        this.id = id;
        this.name = title;
        this.club = comp;
        this.city = city;
        this.address = addr;
        this.description = descr;
        this.day = day;
        this.month = month;
        this.year = year;
        this.startHour = starth;
        this.endHour = endh;
        this.numAssistants = numA;
        this.dress = dress;
        this.age = age;
        this.music = music;
        this.lati = lati;
        this.longi = longi;
        this.webPay= webPay;

        if(blists != null)this.blists = blists;
        else this.blists = false;
        if(btickets != null)this.btickets = btickets;
        else this.btickets = false;
        if(bvips != null)this.bvips = bvips;
        else this.bvips = false;

        this.listsDescr = listsDescr;
        this.ticketsDescr = ticketsDescr;
        this.vipsDescr = vipsDescr;

        this.listsPrice = listsPrice;
        this.ticketsPrice = ticketsPrice;
        this.vipsPrice = vipsPrice;
    }

    public Event(String id, String title, String comp, Boolean blists) {
        this.id = id;
        this.name = title;
        this.club = comp;
        this.blists = blists;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getClub() {
        return club;
    }
    public void setClub(String club) {
        this.club = club;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
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

    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

    public int getNumAssistants() {
        return numAssistants;
    }

    public String getStartHour() {
        return startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public String getDress() {
        return dress;
    }

    public String getAge() {
        return age;
    }

    public String getMusic() {
        return music;
    }

    public String getListsDescr(){
        return listsDescr;
    }
    public String getTicketsDescr(){
        return ticketsDescr;
    }
    public String getVipsDescr(){
        return vipsDescr;
    }

    public int getListsPrice(){
        return listsPrice;
    }
    public int getTicketsPrice(){
        return ticketsPrice;
    }
    public int getVipsPrice(){
        return vipsPrice;
    }

    public Boolean hasLists() {
        return blists;
    }
    public Boolean hasTickets() {
        return btickets;
    }
    public Boolean hasVips() {
        return bvips;
    }

    public String getWebPay() { return webPay;}

    
}
