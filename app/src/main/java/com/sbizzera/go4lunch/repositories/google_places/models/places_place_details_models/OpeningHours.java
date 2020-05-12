package com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models;

import com.google.gson.annotations.SerializedName;

public class OpeningHours{
    @SerializedName("open_now")
    private Boolean openNow;

    public OpeningHours(Boolean openNow) {
        this.openNow = openNow;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

}
