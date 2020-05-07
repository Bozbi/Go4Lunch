package com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyResults {

    @SerializedName("results")
    @Expose
    private List<NearbyPlace> restaurantList;

    public List<NearbyPlace> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<NearbyPlace> restaurantList) {
        this.restaurantList = restaurantList;
    }


}

