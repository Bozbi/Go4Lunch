package com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models;

import com.google.gson.annotations.SerializedName;


public class AddressComponent {
    @SerializedName("long_name")
    private String value;

    public AddressComponent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

