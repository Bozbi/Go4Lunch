package com.sbizzera.go4lunch.view_models;


import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.CustomMapMarker;
import com.sbizzera.go4lunch.model.MapFragmentModel;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentViewModel extends ViewModel {

    private static final String TAG = "MapFragmentViewModel";

    private MediatorLiveData<MapFragmentModel> mUiModelLiveData = new MediatorLiveData<>();
    private LocationService mLocator;
    private PermissionService mPermissionService;
    private GooglePlacesService mGooglePlacesService;
    private FireStoreService mFireStoreService;

    private LiveData<Boolean> fineLocationPermissionLiveData;
    private LiveData<Location> locationLiveData;
    private LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData;


    MapFragmentViewModel(LocationService locator, PermissionService permissionService, GooglePlacesService googlePlacesService, FireStoreService fireStoreService) {
        mLocator = locator;
        mPermissionService = permissionService;
        mGooglePlacesService = googlePlacesService;
        mFireStoreService = fireStoreService;
        wireUpMediator();

    }

    public LiveData<MapFragmentModel> getUIModel() {
        return mUiModelLiveData;
    }


    public void wireUpMediator() {

        LiveData<Boolean> fineLocationPermissionLiveData = mPermissionService.getPermissionLiveData();
        LiveData<Location> locationLiveData = mLocator.getLocation();
        LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData = Transformations.switchMap(locationLiveData, location -> mGooglePlacesService.getNearbyRestaurants(Go4LunchUtils.locationToString(location)));
        LiveData<List<FireStoreRestaurant>> fireStoreRestaurantsLiveData = mFireStoreService.getAllKnownRestaurants();

        mUiModelLiveData.addSource(fineLocationPermissionLiveData, fineLocationPermission -> {
            mUiModelLiveData.postValue(combineSources(locationLiveData.getValue(), fineLocationPermission, nearbyRestaurantsLiveData.getValue(), fireStoreRestaurantsLiveData.getValue()));
        });

        mUiModelLiveData.addSource(locationLiveData, location -> {
            mUiModelLiveData.postValue(combineSources(location, fineLocationPermissionLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), fireStoreRestaurantsLiveData.getValue()));
        });

        mUiModelLiveData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> {
            mUiModelLiveData.postValue(combineSources(locationLiveData.getValue(), fineLocationPermissionLiveData.getValue(), nearbyRestaurants, fireStoreRestaurantsLiveData.getValue()));
        });

        mUiModelLiveData.addSource(fireStoreRestaurantsLiveData, fireStoreRestaurants -> {
            mUiModelLiveData.postValue(combineSources(locationLiveData.getValue(), fineLocationPermissionLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), fireStoreRestaurants));
        });

    }


    private MapFragmentModel combineSources(Location location, Boolean fineLocationPermission, List<NearbyPlace> restaurantslist, List<FireStoreRestaurant> fireStoreRestaurants) {
        List<CustomMapMarker> listMapMarkers = createMarkers(restaurantslist, fireStoreRestaurants);
        MapFragmentModel model = new MapFragmentModel();
        model.setFineLocationPermission(fineLocationPermission);
        model.setLocation(location);
        model.setRestaurantsList(restaurantslist);
        model.setMapMarkersList(listMapMarkers);
        return model;
    }

    private List<CustomMapMarker> createMarkers(List<NearbyPlace> restaurantslist, List<FireStoreRestaurant> fireStoreRestaurants) {
        List<CustomMapMarker> markersListToReturn = new ArrayList<>();
        if (restaurantslist != null) {
            for (NearbyPlace nearbyRestaurant : restaurantslist) {
                CustomMapMarker marker = new CustomMapMarker(
                        nearbyRestaurant.getLat(),
                        nearbyRestaurant.getLng(),
                        R.drawable.ic_restaurant_marker_icon_red,
                        nearbyRestaurant.getName(),
                        nearbyRestaurant.getId()
                );
                markersListToReturn.add(marker);
            }
        }
        if (fireStoreRestaurants != null) {
            for (FireStoreRestaurant fireStoreRestaurant : fireStoreRestaurants) {
                //DeleteMarker if restaurant already in Nearby Search
                for (CustomMapMarker marker : new ArrayList<>(markersListToReturn)) {
                    if (marker.getRestaurantId().equals(fireStoreRestaurant.getRestaurantId())) {
                        markersListToReturn.remove(marker);
                    }
                }
                CustomMapMarker marker = new CustomMapMarker(
                        fireStoreRestaurant.getLat(),
                        fireStoreRestaurant.getLng(),
                        R.drawable.ic_restaurant_marker_icon,
                        fireStoreRestaurant.getName(),
                        fireStoreRestaurant.getRestaurantId()
                );
                markersListToReturn.add(marker);
            }
        }
        return markersListToReturn;
    }

    public void updatePermissionAndLocation() {
        mPermissionService.checkPermission();
        mLocator.getLocation();
    }


}
