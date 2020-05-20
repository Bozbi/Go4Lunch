package com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyLocation {
    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;

    public NearbyLocation(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

     Double getLat() {
        return lat;
    }

     Double getLng() {
        return lng;
    }

}
