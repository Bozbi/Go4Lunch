package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;


import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.services.CameraPositionRepo;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import timber.log.Timber;

public class MapFragmentViewModel extends ViewModel {

    private LocationService mLocator;
    private PermissionService mPermissionService;
    private GooglePlacesService mGooglePlacesService;
    private FireStoreService mFireStoreService;

    private MediatorLiveData<MapFragmentModel> mUiModelLiveData = new MediatorLiveData<>();

    // TODO MEDIATOR : EITHER CAMERA OR IF NO CAMERA SET : USE USERLOCATION
    private MediatorLiveData<List<NearbyPlace>> nearbyRestaurantsLiveData;

    private LiveData<Location> userLocationLD;
    private LiveData<List<FireStoreRestaurant>> fireStoreRestaurantsLiveData;
    private LiveData<VisibleRegion> userSearchAreaLiveData = new MutableLiveData<>();

    private int nearbySearchRadius = 500;
    private CameraPositionRepo mCameraPositionRepo;
    private Boolean mMapIsReady = false;


    public MapFragmentViewModel(LocationService locator, GooglePlacesService googlePlacesService, FireStoreService fireStoreService, PermissionService permissionService, CameraPositionRepo cameraPositionRepo) {
        mLocator = locator;
        mGooglePlacesService = googlePlacesService;
        mFireStoreService = fireStoreService;
        mPermissionService = permissionService;
        mCameraPositionRepo = cameraPositionRepo;

        wireUpMediator();
        Timber.d("Creating a viewModel");
    }

    public LiveData<MapFragmentModel> getUIModel() {
        return mUiModelLiveData;
    }


    public void wireUpMediator() {

        //nearbyRestaurantsLiveData = Transformations.switchMap(userLocationLD, location -> mGooglePlacesService.getNearbyRestaurants(Go4LunchUtils.locationToString(location), nearbySearchRadius));

        // TODO BORIS LINK TO NEARBY RESTAUS MEDIATOR
        userLocationLD = mLocator.getLocationLD();
        fireStoreRestaurantsLiveData = mFireStoreService.getAllKnownRestaurants();

        mUiModelLiveData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> {
            combineSources(nearbyRestaurants, fireStoreRestaurantsLiveData.getValue(), userLocationLD.getValue(), userSearchAreaLiveData.getValue());
        });

        mUiModelLiveData.addSource(fireStoreRestaurantsLiveData, fireStoreRestaurants -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), fireStoreRestaurants, userLocationLD.getValue(), userSearchAreaLiveData.getValue());
        });

        mUiModelLiveData.addSource(userLocationLD, userLocation -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), fireStoreRestaurantsLiveData.getValue(), userLocation, userSearchAreaLiveData.getValue());
        });

        mUiModelLiveData.addSource(userSearchAreaLiveData, region -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), fireStoreRestaurantsLiveData.getValue(), userLocationLD.getValue(), region);
        });
    }

    private void combineSources(
        @Nullable List<NearbyPlace> restaurantsList,
        @Nullable List<FireStoreRestaurant> fireStoreRestaurants,
        @Nullable Location userLocation,
        @Nullable VisibleRegion region,
        @Nullable VisibleRegion oldRegion
    ) {
        if (mMapIsReady) {

            CameraPosition lastCameraPositionKnown = null;
            if (mCameraPositionRepo.getLastCameraPosition() != null) {
                lastCameraPositionKnown = mCameraPositionRepo.getLastCameraPosition();
            } else if (userLocation != null) {
                lastCameraPositionKnown = fromLocationToCameraPosition(userLocation);
            }

            // TODO BORIS USE REGION AND OLD REGION FOR
            List<CustomMapMarker> listMapMarkers = createMarkers(restaurantsList, fireStoreRestaurants);
            mUiModelLiveData.setValue(new MapFragmentModel(listMapMarkers, lastCameraPositionKnown, isSearchButtonVisible));
        }
    }

    private CameraPosition fromLocationToCameraPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return new CameraPosition(latLng, 15, 0, 0);
    }

    private List<CustomMapMarker> createMarkers(
        @Nullable List<NearbyPlace> restaurantsList,
        @Nullable List<FireStoreRestaurant> fireStoreRestaurants
    ) {
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
            combineSources(nearbyPlaces, fireStoreRestaurantsLiveData.getValue(), null, region);
        });

        actionLE.setValue(ViewAction.FETCH_NEW_AREA_INVISIBLE);
    }

    public void setLastCameraPosition(CameraPosition cameraPosition) {
        mCameraPositionRepo.setLastCameraPosition(cameraPosition);
    }

    public void setLastVisibleRegion(VisibleRegion visibleRegion) {
        mCameraPositionRepo.setLastVisibleRegion(visibleRegion);
    }

    public void onResume() {
        mLocator.refresh();
    }

    public enum ViewAction {
        FETCH_NEW_AREA_VISIBLE,
        FETCH_NEW_AREA_INVISIBLE
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.d("cleared");
    }

    public void mapIsReady(Boolean bol) {
        mMapIsReady = bol;
    }
}
