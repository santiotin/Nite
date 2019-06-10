package com.santiotin.nite.Models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {

    private String uid;
    private String name;


    public User() {

    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
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

}
