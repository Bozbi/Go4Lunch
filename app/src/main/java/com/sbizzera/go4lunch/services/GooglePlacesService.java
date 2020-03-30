package com.sbizzera.go4lunch.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class GooglePlacesService {

    private static final String TAG = "RestaurantRepository";

    private static GooglePlacesService sGooglePlacesService;

    public static GooglePlacesService getInstance() {
        if (sGooglePlacesService == null) {
            sGooglePlacesService = new GooglePlacesService();
        }
        return sGooglePlacesService;
    }

    private GooglePlacesAPI mGooglePlacesAPI;

    public GooglePlacesService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mGooglePlacesAPI = retrofit.create(GooglePlacesAPI.class);
    }

    public LiveData<List<NearbyPlace>> getNearbyRestaurants(String location) {
        MutableLiveData<List<NearbyPlace>> nearbyRestaurantListLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getNearbyRestaurant(location).enqueue(new Callback<NearbyResults>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                assert response.body() != null;
                nearbyRestaurantListLiveData.postValue(response.body().getRestaurantList());
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
                nearbyRestaurantListLiveData.postValue(null);
            }
        });

        return nearbyRestaurantListLiveData;
    }


    public DetailsResponse.DetailResult getRestaurantDetailsById(String id) {

        DetailsResponse.DetailResult placeDetail = null;
        try {
            // TODO if body not null
            placeDetail = mGooglePlacesAPI.getRestaurantDetailsById(id).execute().body().getDetailResult();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return placeDetail;
    }


}
