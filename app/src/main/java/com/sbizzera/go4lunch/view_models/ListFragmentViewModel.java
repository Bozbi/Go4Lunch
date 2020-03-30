package com.sbizzera.go4lunch.view_models;

import android.location.Location;
import android.os.AsyncTask;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.ListFragmentAdapterModel;
import com.sbizzera.go4lunch.model.ListFragmentModel;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFragmentViewModel extends ViewModel {

    private static final String TAG = "ListFragmentViewModel";

    private LocationService locator;
    private GooglePlacesService googlePlacesService;
    private FireStoreService fireStoreService;

    private MediatorLiveData<ListFragmentModel> modelLiveData = new MediatorLiveData<>();

    private MutableLiveData<Map<String, DetailsResponse.DetailResult>> detailsMapLD = new MutableLiveData<>();

    public ListFragmentViewModel(LocationService locator, GooglePlacesService googlePlacesService, FireStoreService fireStoreService) {
        this.locator = locator;
        this.googlePlacesService = googlePlacesService;
        this.fireStoreService = fireStoreService;
        detailsMapLD.setValue(new HashMap<>());
        wireUpMediator();
    }

    public LiveData<ListFragmentModel> getModel() {
        return modelLiveData;
    }

    private void wireUpMediator() {
        LiveData<Location> locationLiveData = locator.getLocation();
        LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData = Transformations.switchMap(locationLiveData,
                location -> googlePlacesService.getNearbyRestaurants(Go4LunchUtils.locationToString(location)));
        LiveData<List<FireStoreRestaurant>> knownRestaurantsLiveData = fireStoreService.getAllKnownRestaurants();

        modelLiveData.addSource(nearbyRestaurantsLiveData, nearbyPlaces -> {
            combineSources(nearbyPlaces, knownRestaurantsLiveData.getValue(), detailsMapLD.getValue());
        });

        modelLiveData.addSource(knownRestaurantsLiveData, knownRestaurants -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), knownRestaurants, detailsMapLD.getValue());
        });

        modelLiveData.addSource(detailsMapLD, detailsMap -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), knownRestaurantsLiveData.getValue(), detailsMap);
        });

    }

    private void combineSources(
            List<NearbyPlace> nearbyPlaces,
            List<FireStoreRestaurant> knownRestaurants,
            Map<String, DetailsResponse.DetailResult> detailsMap) {

        if (nearbyPlaces == null && knownRestaurants == null) {
            return;
        }
        List<ListFragmentAdapterModel> results = new ArrayList<>();
        if (nearbyPlaces != null) {
            for (NearbyPlace place : nearbyPlaces) {
                DetailsResponse.DetailResult placeDetail = detailsMap.get(place.getId());
                if (placeDetail == null) {
                    new DetailResultAsyncTask(place.getId(), new WeakReference<>(this), googlePlacesService).execute();
                    results.add(new ListFragmentAdapterModel(
                            place.getName(),
                            place.getId(),
                            null,
                            null,
                            R.color.missingInfoColor,
                            //TODO
                            "0",
                            "0",
                            View.INVISIBLE,
                            View.INVISIBLE,
                            View.INVISIBLE,
                            null
                    ));
                }else{
                    results.add(new ListFragmentAdapterModel(
                            place.getName(),
                            place.getId(),
                            placeDetail.getAddressComponentList().get(1).getValue(),
                            null,
                            R.color.missingInfoColor,
                            //TODO
                            "0",
                            "0",
                            View.INVISIBLE,
                            View.INVISIBLE,
                            View.INVISIBLE,
                            placeDetail.getPhotosList().get(0).getPhotoReference()
                            )
                    );
                }
            }
        }
        modelLiveData.setValue(new ListFragmentModel(results));
    }

    private static class DetailResultAsyncTask extends AsyncTask<Void, Void, DetailsResponse.DetailResult> {

        private String restaurantId;
        private WeakReference<ListFragmentViewModel> viewModelRef;
        private GooglePlacesService googlePlacesService;

        public DetailResultAsyncTask(String restaurantId, WeakReference<ListFragmentViewModel> viewModelRef, GooglePlacesService googlePlacesService) {
            super();
            this.restaurantId = restaurantId;
            this.viewModelRef = viewModelRef;
            this.googlePlacesService = googlePlacesService;
        }

        @Override
        protected DetailsResponse.DetailResult doInBackground(Void... voids) {
            return googlePlacesService.getRestaurantDetailsById(restaurantId);
        }

        @Override
        protected void onPostExecute(DetailsResponse.DetailResult detailResult) {

            if (viewModelRef.get() != null) {
                Map<String, DetailsResponse.DetailResult> map = viewModelRef.get().detailsMapLD.getValue();
                map.put(restaurantId, detailResult);
                viewModelRef.get().detailsMapLD.setValue(map);
            }
        }
    }
}
