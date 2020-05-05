package com.sbizzera.go4lunch.model.places_place_details_models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.internal.EverythingIsNonNull;

public class DetailsResponse {

    @SerializedName("result")
    private DetailResult detailResult;

    @EverythingIsNonNull
    public DetailResult getDetailResult() {
        return detailResult;
    }


}
