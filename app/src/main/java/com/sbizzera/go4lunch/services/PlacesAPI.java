package com.sbizzera.go4lunch.services;


import com.sbizzera.go4lunch.Commons;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesAPI {

    @GET("maps/api/place/nearbysearch/json?radius=1000&type=restaurant&key="+ Commons.PLACES_API_KEY)
    Call<NearbyResults> getNearbyRestaurant(@Query("location")String location);
}
