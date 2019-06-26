package com.santiotin.nite.Models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {

    private String uid;
    private String name;
    private String age;
    private String city;
    private String email;
    private Long numEvents;
    private Long numFollowers;
    private Long numFollowing;
    private Long photoTime;

    public User() {

    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public User (String uid, String name, String age, String city, String email, Long events, Long follow, Long folling, Long photoTime){
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.city = city;
        this.email = email;
        this.numEvents = events;
        this.numFollowers = follow;
        this.numFollowing = folling;
        this.photoTime = photoTime;
    }

    public static class ChildClass implements Serializable {

        public ChildClass() {}
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public String getAge(){
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public Long getNumEvents() {
        return numEvents;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public Long getNumFollowing() {
        return numFollowing;
    }

    public Long getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(Long photoTime) {
        this.photoTime = photoTime;
    }
}
