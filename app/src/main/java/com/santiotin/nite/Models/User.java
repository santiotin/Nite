package com.santiotin.nite.Models;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private int image;

    public User() {

    }

    public User(String name, int img) {
        this.name = name;
        this.image = img;
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

    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
}
