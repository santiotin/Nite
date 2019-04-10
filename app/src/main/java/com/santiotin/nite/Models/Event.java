package com.santiotin.nite.Models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String title;
    private String company;
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

    public Event(String title, String comp, String addr, String descr, Date date, int starth, int endh, int price, int numA, int img) {
        this.title = title;
        this.company = comp;
        this.address = addr;
        this.description = descr;
        this.date = date;
        this.startHour = starth;
        this.endHour = endh;
        this.price = price;
        this.numAssistants = numA;
        this.image = img;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
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
