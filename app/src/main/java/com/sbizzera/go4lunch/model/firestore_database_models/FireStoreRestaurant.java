package com.sbizzera.go4lunch.model.firestore_database_models;

public class FireStoreRestaurant {

    private String restaurantID;
    private String name;

    private Double lat;

    private Double lng;

    public FireStoreRestaurant() {
    }

    public FireStoreRestaurant(String restaurantID, String name,Double lat, Double lng) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
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
}
