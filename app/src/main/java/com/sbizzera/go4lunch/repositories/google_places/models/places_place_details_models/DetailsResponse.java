package com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models;


import com.google.gson.annotations.SerializedName;

import retrofit2.internal.EverythingIsNonNull;

public class DetailsResponse {

    @SerializedName("result")
    private DetailResult detailResult;

    @EverythingIsNonNull
    public DetailResult getDetailResult() {
        return detailResult;
    }


}
