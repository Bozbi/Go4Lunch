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
    private MutableLiveData<List<NearbyPlace>> mNearbyRestaurants = new MutableLiveData<>();
    private MutableLiveData<DetailsResponse.DetailResult> mPlaceDetailsLiveData = new MutableLiveData<>();


    public RestaurantRepository() {
        Retrofit retrofit = new Retrofit.Builder()
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
                Log.d(TAG, "NearbyRestaurants response: " + response.body().getRestaurantList().get(0).getName() + ",... " + response.body().getRestaurantList().size() + " restaurants");
                mNearbyRestaurants.postValue(response.body().getRestaurantList());
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
                Log.d(TAG, "NearbyRestaurants request failed");
                mNearbyRestaurants.postValue(null);
            }
        });

        return mNearbyRestaurants;
    }


    public LiveData<DetailsResponse.DetailResult> getRestaurantDetailsById(String id) {
        MutableLiveData<DetailsResponse.DetailResult> restaurantDetailsLiveData = new MutableLiveData<>();
        mPlacesAPI.getRestaurantDetailsById(id).enqueue(new Callback<DetailsResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                //TODO try to put out this exception
                restaurantDetailsLiveData.postValue(response.body().getDetailResult());
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<DetailsResponse> call, Throwable t) {
                restaurantDetailsLiveData.postValue(null);
            }
        });

        return mPlaceDetailsLiveData;
    }

}
