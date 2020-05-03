package com.sbizzera.go4lunch.services;

import android.util.ArrayMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.BuildConfig;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;
import timber.log.Timber;

public class GooglePlacesService {

    private static final String TAG = "RestaurantRepository";
    private static GooglePlacesService sGooglePlacesService;

    //CACHING FETCHEDRESTAURANTS
    private Map<String,NearbyPlace> mNearbyPlaceMapCached = new HashMap<>();

    //CACHING RESTAURANTSDETAILS
    private Map<String, DetailsResponse.DetailResult> mRestaurantsDetailsMapCached = new HashMap<>();

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

    public LiveData<List<NearbyPlace>> getNearbyRestaurants(String location, int radius) {

        MutableLiveData<List<NearbyPlace>> nearbyRestaurantListLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getNearbyRestaurant(location, radius,BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<NearbyResults>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                assert response.body() != null;
                for (NearbyPlace restaurant : response.body().getRestaurantList()) {
                    if(mNearbyPlaceMapCached.get(restaurant.getId())==null){
                        mNearbyPlaceMapCached.put(restaurant.getId(),restaurant);
                    }

                }
                List<NearbyPlace> listToReturn = new ArrayList<>(mNearbyPlaceMapCached.values());
                nearbyRestaurantListLiveData.postValue(listToReturn);
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
            }
        });

        return nearbyRestaurantListLiveData;
    }


    public DetailsResponse.DetailResult getRestaurantDetailsByIdAsync(String id) {
        if (mRestaurantsDetailsMapCached.get(id) != null) {
            return mRestaurantsDetailsMapCached.get(id);
        }
        DetailsResponse.DetailResult placeDetail = null;
        try {
            Response<DetailsResponse> response = mGooglePlacesAPI.getRestaurantDetailsById(id,BuildConfig.GOOGLE_API_KEY).execute();
            if (response.body() != null && response.body().getDetailResult() != null) {
                placeDetail = response.body().getDetailResult();
                mRestaurantsDetailsMapCached.put(id, placeDetail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return placeDetail;
    }

    public LiveData<DetailsResponse.DetailResult> getRestaurantDetailsById(String id) {
        if (mRestaurantsDetailsMapCached.get(id) != null) {
            return new MutableLiveData<>(mRestaurantsDetailsMapCached.get(id));
        }
        MutableLiveData<DetailsResponse.DetailResult> restaurantDetailsLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getRestaurantDetailsById(id,BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<DetailsResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                if (response.body() != null) {
                    restaurantDetailsLiveData.postValue(response.body().getDetailResult());
                    mRestaurantsDetailsMapCached.put(id, response.body().getDetailResult());
                } else {
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

    public List<NearbyPlace> getNearbyCache (){
        return new ArrayList<>(mNearbyPlaceMapCached.values());
    }
}
