package com.sbizzera.go4lunch.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;

import java.io.IOException;
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

    //CACHING NEARBYSEARCH
    private MutableLiveData<List<NearbyPlace>> mNearbyCache = new MutableLiveData<>();
    private String mLocationCache;
    private int mRadiusCache;

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

        if (location.equals(mLocationCache) && mRadiusCache == radius) {
            return mNearbyCache;
        }
        MutableLiveData<List<NearbyPlace>> nearbyRestaurantListLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getNearbyRestaurant(location, radius).enqueue(new Callback<NearbyResults>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                assert response.body() != null;
                nearbyRestaurantListLiveData.postValue(response.body().getRestaurantList());
                mLocationCache = location;
                mRadiusCache = radius;
                mNearbyCache = nearbyRestaurantListLiveData;
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
                nearbyRestaurantListLiveData.postValue(null);
            }
        });

        return nearbyRestaurantListLiveData;
    }


    public DetailsResponse.DetailResult getRestaurantDetailsByIdAsync(String id) {
        if (mRestaurantsDetailsMapCached.get(id) != null) {
            Timber.d("Restaurant in cache");
            return mRestaurantsDetailsMapCached.get(id);
        }
        DetailsResponse.DetailResult placeDetail = null;
        try {
            Response<DetailsResponse> response = mGooglePlacesAPI.getRestaurantDetailsById(id).execute();
            if (response.body() != null && response.body().getDetailResult() != null) {
                placeDetail = response.body().getDetailResult();
                Timber.d("restaurant fetched");
                mRestaurantsDetailsMapCached.put(id, placeDetail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return placeDetail;
    }

    public LiveData<DetailsResponse.DetailResult> getRestaurantDetailsById(String id) {
        if (mRestaurantsDetailsMapCached.get(id) != null) {
            Timber.d("Restaurant in cache");
            return new MutableLiveData<>(mRestaurantsDetailsMapCached.get(id));
        }
        MutableLiveData<DetailsResponse.DetailResult> restaurantDetailsLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getRestaurantDetailsById(id).enqueue(new Callback<DetailsResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                if (response.body() != null) {
                    restaurantDetailsLiveData.postValue(response.body().getDetailResult());
                    mRestaurantsDetailsMapCached.put(id, response.body().getDetailResult());
                    Timber.d("restaurant fetched");

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


}
