package com.sbizzera.go4lunch;


import com.sbizzera.go4lunch.model.NearbyResults;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesAPI {

    @GET("maps/api/place/nearbysearch/json?")
    Call<NearbyResults> getNearbyRestaurant(@Query("location")String location,
                                            @Query("radius")int radius,
                                            @Query("type") String type,
                                            @Query("key")String key);
}
