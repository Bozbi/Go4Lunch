package com.sbizzera.go4lunch.model.places_nearby_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyGeometry{
    @SerializedName("location")
    @Expose
    private NearbyLocation location;

    public NearbyLocation getLocation() {
        return location;
    }

    public void setLocation(NearbyLocation location) {
        this.location = location;
    }

}
