package com.sbizzera.go4lunch.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;
import com.sbizzera.go4lunch.utils.Commons;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class RestaurantRepository {

    private static final String TAG = "RestaurantRepository";

    private static RestaurantRepository sRestaurantRepository;

    public static RestaurantRepository getInstance() {
        if (sRestaurantRepository == null) {
            sRestaurantRepository = new RestaurantRepository();
        }
        return sRestaurantRepository;
    }

    private PlacesAPI mPlacesAPI;

    public RestaurantRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlacesAPI = retrofit.create(PlacesAPI.class);
    }

    public LiveData<List<NearbyPlace>> getNearbyRestaurants(String location) {
        MutableLiveData<List<NearbyPlace>> nearbyRestaurantListLiveData = new MutableLiveData<>();
        mPlacesAPI.getNearbyRestaurant(location).enqueue(new Callback<NearbyResults>() {
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


    public LiveData<DetailsResponse.DetailResult> getRestaurantDetailsById(String id) {
        MutableLiveData<DetailsResponse.DetailResult> restaurantDetailsLiveData = new MutableLiveData<>();
        mPlacesAPI.getRestaurantDetailsById(id).enqueue(new Callback<DetailsResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                if (response.body()!= null){
                    restaurantDetailsLiveData.postValue(response.body().getDetailResult());
                }else {
                    restaurantDetailsLiveData.postValue(null);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<DetailsResponse> call, Throwable t) {
                restaurantDetailsLiveData.postValue(null);
            }
        });

        return restaurantDetailsLiveData;
    }

}
