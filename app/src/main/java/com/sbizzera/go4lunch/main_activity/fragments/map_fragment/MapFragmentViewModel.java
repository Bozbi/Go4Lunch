package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;


import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentViewModel extends ViewModel {

    private MediatorLiveData<MapFragmentModel> mUiModelLiveData = new MediatorLiveData<>();
    private LocationService mLocator;
    private GooglePlacesService mGooglePlacesService;
    private FireStoreService mFireStoreService;
    private LiveData<Location> userLocationLD;
    private LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData;
    private LiveData<List<FireStoreRestaurant>> fireStoreRestaurantsLiveData;
    private SingleLiveEvent<ViewAction> actionLE = new SingleLiveEvent<>();
    private SingleLiveEvent<Location> locationFoundLE ;
    private int nearbySearchRadius = 1000;


    public MapFragmentViewModel(LocationService locator, GooglePlacesService googlePlacesService, FireStoreService fireStoreService) {
        mLocator = locator;
        mGooglePlacesService = googlePlacesService;
        mFireStoreService = fireStoreService;
        locationFoundLE = mLocator.getLocationLE();
        wireUpMediator();
    }

    public LiveData<MapFragmentModel> getUIModel() {
        return mUiModelLiveData;
    }

    public LiveData<ViewAction> getAction() {
        return actionLE;
    }

    public LiveData<Location> getLocationLE() {
        return locationFoundLE;
    }

    public void wireUpMediator() {

        userLocationLD = mLocator.getLocationLD();
        nearbyRestaurantsLiveData = Transformations.switchMap(userLocationLD, location -> mGooglePlacesService.getNearbyRestaurants(Go4LunchUtils.locationToString(location), nearbySearchRadius));
        fireStoreRestaurantsLiveData =  mFireStoreService.getAllKnownRestaurants();

        mUiModelLiveData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> {
            combineSources(nearbyRestaurants, fireStoreRestaurantsLiveData.getValue());
        });

        mUiModelLiveData.addSource(fireStoreRestaurantsLiveData, fireStoreRestaurants -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), fireStoreRestaurants);
        });

    }

    private void combineSources(List<NearbyPlace> restaurantsList, List<FireStoreRestaurant> fireStoreRestaurants) {
        if (userLocationLD != null) {
            List<CustomMapMarker> listMapMarkers = createMarkers(restaurantsList, fireStoreRestaurants);
            mUiModelLiveData.setValue(new MapFragmentModel(listMapMarkers));
        }
    }

    private List<CustomMapMarker> createMarkers(List<NearbyPlace> restaurantsList, List<FireStoreRestaurant> fireStoreRestaurants) {
        List<CustomMapMarker> markersListToReturn = new ArrayList<>();
        if (restaurantsList != null) {
            for (NearbyPlace nearbyRestaurant : restaurantsList) {
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


    private int getRadiusShownFromBounds(LatLngBounds latLngBounds) {
        double southEastLat = latLngBounds.southwest.latitude;
        double southEastLng = latLngBounds.northeast.longitude;
        Location southEast = new Location("");
        southEast.setLatitude(southEastLat);
        southEast.setLongitude(southEastLng);
        double southWestLat = latLngBounds.southwest.latitude;
        double southWestLng = latLngBounds.southwest.longitude;
        Location southWest = new Location("");
        southWest.setLatitude(southWestLat);
        southWest.setLongitude(southWestLng);

        return (int) (southEast.distanceTo(southWest) / 2);
    }

    public void shouldNewAreaFetchBeVisible(LatLng cameraLatLng, LatLngBounds cameraBounds) {
        int cameraRadius = getRadiusShownFromBounds(cameraBounds);
        int radiusDiff = Math.abs(cameraRadius - nearbySearchRadius);
        if (radiusDiff > 400) {
            actionLE.setValue(ViewAction.FETCH_NEW_AREA_VISIBLE);
        }

        Location cameraLocation = new Location("");
        cameraLocation.setLatitude(cameraLatLng.latitude);
        cameraLocation.setLongitude(cameraLatLng.longitude);


        int distanceDiff = (int) cameraLocation.distanceTo(userLocationLD.getValue());
        if (distanceDiff > 200) {
            actionLE.setValue(ViewAction.FETCH_NEW_AREA_VISIBLE);
        }

    }


    public void changeLocationSourceLD(LatLng cameraLocation, LatLngBounds bounds) {
        Location locationToFetch = new Location("");
        locationToFetch.setLatitude(cameraLocation.latitude);
        locationToFetch.setLongitude(cameraLocation.longitude);
        int radiusToFetch = getRadiusShownFromBounds(bounds);
        nearbySearchRadius = radiusToFetch;
        userLocationLD = new MutableLiveData<>(locationToFetch);
        mUiModelLiveData.removeSource(nearbyRestaurantsLiveData);
        LiveData<List<NearbyPlace>> nearbyPlacesLD = mGooglePlacesService.getNearbyRestaurants(Go4LunchUtils.locationToString(locationToFetch), radiusToFetch);

        mUiModelLiveData.addSource(nearbyPlacesLD, nearbyPlaces -> {
            combineSources(nearbyPlaces, fireStoreRestaurantsLiveData.getValue());
        });

        actionLE.setValue(ViewAction.FETCH_NEW_AREA_INVISIBLE);
    }

    public enum ViewAction {
        FETCH_NEW_AREA_VISIBLE,
        FETCH_NEW_AREA_INVISIBLE
    }

}
