package com.sbizzera.go4lunch.model.places_place_details_models;

import com.google.gson.annotations.SerializedName;

public class Geometry{
    @SerializedName("location")
    private Location location;

    public Location getLocation() {
        return location;
    }

}
