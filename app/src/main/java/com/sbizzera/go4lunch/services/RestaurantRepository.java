package com.sbizzera.go4lunch.services;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class RestaurantRepository {

    private static RestaurantRepository sRestaurantRepository;
    public static RestaurantRepository getInstance() {
        if (sRestaurantRepository == null) {
            sRestaurantRepository = new RestaurantRepository();
        }
        return sRestaurantRepository;
    }

    private PlacesAPI mPlacesAPI;
    private MutableLiveData<List<NearbyPlace>> mNearbyRestaurants = new MutableLiveData<>();



    public RestaurantRepository() {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlacesAPI = retrofit.create(PlacesAPI.class);
    }

    public LiveData<List<NearbyPlace>> getNearbyRestaurants(String location) {

        mPlacesAPI.getNearbyRestaurant(location).enqueue(new Callback<NearbyResults>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                assert response.body() != null;
                mNearbyRestaurants.postValue(response.body().getRestaurantList());
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
                mNearbyRestaurants.postValue(null);
            }
        });

        return mNearbyRestaurants;
    }


}
