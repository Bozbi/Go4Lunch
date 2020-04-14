package com.sbizzera.go4lunch.services;


import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;
import com.sbizzera.go4lunch.utils.Commons;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/json?type=restaurant&key=" + Commons.PLACES_API_KEY)
    Call<NearbyResults> getNearbyRestaurant(@Query("location") String location,
                                            @Query("radius")int radius);


    @GET("maps/api/place/details/json?fields=place_id,website,name,formatted_phone_number,photos,opening_hours,address_component,geometry&key=" + Commons.PLACES_API_KEY)
    Call<DetailsResponse> getRestaurantDetailsById(@Query("place_id") String id);

}
