package com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models;

import com.google.gson.annotations.SerializedName;

public class Photos{

    @SerializedName("photo_reference")
    private String photoReference;

    public String getPhotoReference() {
        return photoReference;
    }

}