package com.example.photosharing.models;

import android.graphics.Bitmap;

public class User {
    private String id;
    private String email;
    private String username;
    private String bio;
    private String image;
    private Bitmap imageCache;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getImageCache() {
        return imageCache;
    }

    public void setImageCache(Bitmap imageCache) {
        this.imageCache = imageCache;
    }
}
