package com.santiotin.nite.Models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String id;
    private String name;
    private String club;
    private String address;
    private String description;
    private String dress;
    private String music;
    private String age;
    private Date date;
    private int startHour;
    private int endHour;
    private int price;
    private int numAssistants;
    private String uri;

    public Event(){

    }

    public static class ChildClass implements Serializable {

        public ChildClass() {}
    }

    public Event(String id, String title, String comp, String addr, String descr,
                 Date date, int starth, int endh, int numA, String img,
                 String dress, String age, String music) {
        this.id = id;
        this.name = title;
        this.club = comp;
        this.address = addr;
        this.description = descr;
        this.date = date;
        this.startHour = starth;
        this.endHour = endh;
        this.numAssistants = numA;
        this.uri = img;
        this.dress = dress;
        this.age = age;
        this.music = music;
    }

    public Event(String id, String title, String comp, String img) {
        this.id = id;
        this.name = title;
        this.club = comp;
        this.uri = img;
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

    public String getUri() {
        return uri;
    }
    public void setUri(String image) {
        this.uri = image;
    }

    public String getDress() {
        return dress;
    }
    public void setDress(String dress) {
        this.dress = dress;
    }

    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }

    public String getMusic() {
        return music;
    }
    public void setMusic(String music) {
        this.music = music;
    }
}
