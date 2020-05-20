package com.sbizzera.go4lunch.repositories.google_places;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.BuildConfig;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyResults;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.DetailResult;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.DetailsResponse;

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

public class GooglePlacesRepo {

    private static GooglePlacesRepo sGooglePlacesRepo;

    //CACHING FETCHEDRESTAURANTS
    private MutableLiveData<Map<String, NearbyPlace>> mNearbyPlaceMapCachedLD = new MutableLiveData<>(new HashMap<>());

    //CACHING RESTAURANTSDETAILS
    private Map<String, DetailResult> mRestaurantsDetailsMapCached = new HashMap<>();

    public static GooglePlacesRepo getInstance() {
        if (sGooglePlacesRepo == null) {
            sGooglePlacesRepo = new GooglePlacesRepo();
        }
        return sGooglePlacesRepo;
    }

    private GooglePlacesAPI mGooglePlacesAPI;

    private GooglePlacesRepo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mGooglePlacesAPI = retrofit.create(GooglePlacesAPI.class);
    }

    public LiveData<List<NearbyPlace>> getNearbyRestaurants(String location, int radius) {

        MutableLiveData<List<NearbyPlace>> nearbyRestaurantListLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getNearbyRestaurant(location, radius, BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<NearbyResults>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                assert response.body() != null;
                Map<String, NearbyPlace> map = mNearbyPlaceMapCachedLD.getValue();
                assert map != null;
                for (NearbyPlace restaurant : response.body().getRestaurantList()) {
                    if (map.get(restaurant.getId()) == null) {
                        map.put(restaurant.getId(), restaurant);
                    }
                }
                List<NearbyPlace> listToReturn = new ArrayList<>(map.values());
                nearbyRestaurantListLiveData.postValue(listToReturn);
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
            }
        });

        return nearbyRestaurantListLiveData;
    }


    public DetailResult getRestaurantDetailsByIdAsync(String id) {
        if (mRestaurantsDetailsMapCached.get(id) != null) {
            return mRestaurantsDetailsMapCached.get(id);
        }
        DetailResult placeDetail = null;
        try {
            Response<DetailsResponse> response = mGooglePlacesAPI.getRestaurantDetailsById(id, BuildConfig.GOOGLE_API_KEY).execute();
            if (response.body() != null && response.body().getDetailResult() != null) {
                placeDetail = response.body().getDetailResult();
                mRestaurantsDetailsMapCached.put(id, placeDetail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return placeDetail;
    }

    public LiveData<DetailResult> getRestaurantDetailsById(String id) {
        if (mRestaurantsDetailsMapCached.get(id) != null) {
            return new MutableLiveData<>(mRestaurantsDetailsMapCached.get(id));
        }
        MutableLiveData<DetailResult> restaurantDetailsLiveData = new MutableLiveData<>();
        mGooglePlacesAPI.getRestaurantDetailsById(id, BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<DetailsResponse>() {

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


    public LiveData<Map<String, NearbyPlace>> getNearbyCacheLiveData() {
        return mNearbyPlaceMapCachedLD;
    }
}
