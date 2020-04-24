package com.sbizzera.go4lunch.model.firestore_models;

import androidx.annotation.NonNull;

import java.util.List;

public class FireStoreRestaurant {

    private String restaurantId;

    private String name;

    private Double lat;

    private Double lng;

    private List<String> likesIds;

    private int lunchCount;

    public FireStoreRestaurant() {
    }

    public FireStoreRestaurant(String restaurantId, String name, Double lat, Double lng) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public List<String> getLikesIds() {
        return likesIds;
    }

    public void setLikesIds(List<String> likesIds) {
        this.likesIds = likesIds;
    }

    public int getLunchCount() {
        return lunchCount;
    }

    public void setLunchCount(int lunchCount) {
        this.lunchCount = lunchCount;
    }

}
