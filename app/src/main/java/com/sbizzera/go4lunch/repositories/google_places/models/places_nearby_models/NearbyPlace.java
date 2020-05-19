package com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Geometry;

public class NearbyPlace {

    @SerializedName("place_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("geometry")
    private NearbyGeometry geometry;

    public NearbyPlace(String id, String name, NearbyGeometry geometry) {
        this.id = id;
        this.name = name;
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return geometry.getLocation().getLat();
    }

    public Double getLng() {
        return geometry.getLocation().getLng();
    }

    public NearbyGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(NearbyGeometry geometry) {
        this.geometry = geometry;
    }
}
