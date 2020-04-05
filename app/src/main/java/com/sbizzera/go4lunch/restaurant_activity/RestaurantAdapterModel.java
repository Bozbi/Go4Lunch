package com.sbizzera.go4lunch.restaurant_activity;

public class RestaurantAdapterModel {
    private String photoUrl;
    private String text;

    public RestaurantAdapterModel(String photoUrl, String text) {
        this.photoUrl = photoUrl;
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
