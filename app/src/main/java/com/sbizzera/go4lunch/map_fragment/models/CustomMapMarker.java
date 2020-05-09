package com.sbizzera.go4lunch.map_fragment.models;

import androidx.annotation.IntegerRes;

public class CustomMapMarker {
    private Double lat;
    private Double lng;
    @IntegerRes
    private int markerIcon;
    private String restaurantName;
    private String restaurantId;

    public CustomMapMarker(Double lat, Double lng, int markerIcon, String restaurantName, String restaurantId) {
        this.lat = lat;
        this.lng = lng;
        this.markerIcon = markerIcon;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public int getMarkerIcon() {
        return markerIcon;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

}
