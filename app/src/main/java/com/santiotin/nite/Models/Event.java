package com.santiotin.nite.Models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String id;
    private String name;
    private String club;
    private String address;
    private String description;
    private Date date;
    private int startHour;
    private int endHour;
    private int price;
    private int numAssistants;
    private int image;

    public Event(){

    }

    public static class ChildClass implements Serializable {

        public ChildClass() {}
    }

    public Event(String id, String title, String comp, String addr, String descr, Date date, int starth, int endh, int price, int numA, int img) {
        this.id = id;
        this.name = title;
        this.club = comp;
        this.address = addr;
        this.description = descr;
        this.date = date;
        this.startHour = starth;
        this.endHour = endh;
        this.price = price;
        this.numAssistants = numA;
        this.image = img;
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

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public int getStartHour() {
        return startHour;
    }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumAssistants() {
        return numAssistants;
    }
    public void setAssistants(Integer numAssistants) {
        this.numAssistants = numAssistants;
    }

    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
}
