package com.example.fakemessenger.user;


import java.io.Serializable;

public class User implements Serializable {
    private String uId;
    private String username;
    private String imageUrl;


    public User() {
        //no arg constructor is needed
    }

    public User(String uId, String username, String imageUrl) {
        this.uId = uId;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
