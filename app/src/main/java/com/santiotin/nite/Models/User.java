package com.santiotin.nite.Models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {

    private String uid;
    private String name;
    private Uri uri;


    public User() {

    }

    public User(String name, int img) {
        this.name = name;
        this.uri = null;
    }

    public User(String uid, String name, Uri uri) {
        this.uid = uid;
        this.name = name;
        this.uri = uri;
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

    public Uri getUri() {
        return uri;
    }
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
}
