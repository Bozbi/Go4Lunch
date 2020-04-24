package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;


import android.location.Location;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.services.CurrentGPSLocationRepo;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.services.VisibleRegionRepo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MapFragmentViewModel extends ViewModel {

    private CurrentGPSLocationRepo mCurrentGPSLocationRepo;
    private GooglePlacesService mGooglePlacesService;
    private FireStoreService mFireStoreService;
    private PermissionService mPermissionService;
    private VisibleRegionRepo mVisibleRegionRepo;

    private MutableLiveData<Boolean> mIsMapLoadedLD = new MutableLiveData<>(false);
    private MediatorLiveData<MapFragmentModel> mUiModelLiveData = new MediatorLiveData<>();
    private LiveData<Location> mCurrentGPSLocationLD;
    private LiveData<List<FireStoreRestaurant>> fireStoreRestaurantsLD;
    private boolean mCameraHasBeenInitializedToLastPosition = false;
    private MediatorLiveData<Pair<String, Integer>> mNearbyParamsMLD = new MediatorLiveData<>();
    private LiveData<VisibleRegion> mLastRestaurantFetchVisibleRegionLD;


    //TODO find in doc max nearby search radius (it is 50 000 m)


    public MapFragmentViewModel(CurrentGPSLocationRepo currentGPSLocationRepo, GooglePlacesService googlePlacesService, FireStoreService fireStoreService, VisibleRegionRepo visibleRegionRepo, PermissionService permissionService) {
        mCurrentGPSLocationRepo = currentGPSLocationRepo;
        mGooglePlacesService = googlePlacesService;
        mFireStoreService = fireStoreService;
        mVisibleRegionRepo = visibleRegionRepo;
        mPermissionService = permissionService;
        wireUpUIMediator();
        wireUpNearbyParams();
    }


    private void wireUpUIMediator() {
        mCurrentGPSLocationLD = mCurrentGPSLocationRepo.getCurrentGPSLocationLD();
        fireStoreRestaurantsLD = mFireStoreService.getAllKnownRestaurants();
        LiveData<VisibleRegion> lastMapVisibleRegionLD = mVisibleRegionRepo.getLastMapVisibleRegion();
        LiveData<List<NearbyPlace>> listNearbyRestaurantsLD = Transformations.switchMap(mNearbyParamsMLD, (pair) -> {
            if (pair != null) {
                return mGooglePlacesService.getNearbyRestaurants(pair.first, pair.second);
            }else{
                return null;
            }
        });

        mLastRestaurantFetchVisibleRegionLD = mVisibleRegionRepo.getLastNearbyRestaurantsFetchVisibleRegion();

        mUiModelLiveData.addSource(mIsMapLoadedLD, isMapReady -> {
            combineSources(isMapReady, lastMapVisibleRegionLD.getValue(), mCurrentGPSLocationLD.getValue(), fireStoreRestaurantsLD.getValue(), listNearbyRestaurantsLD.getValue(), mLastRestaurantFetchVisibleRegionLD.getValue());
        });

        mUiModelLiveData.addSource(lastMapVisibleRegionLD, lastVisibleRegion -> {
            combineSources(mIsMapLoadedLD.getValue(), lastVisibleRegion, mCurrentGPSLocationLD.getValue(), fireStoreRestaurantsLD.getValue(), listNearbyRestaurantsLD.getValue(), mLastRestaurantFetchVisibleRegionLD.getValue());
        });

        mUiModelLiveData.addSource(mCurrentGPSLocationLD, currentGPSLocation -> {
            combineSources(mIsMapLoadedLD.getValue(), lastMapVisibleRegionLD.getValue(), currentGPSLocation, fireStoreRestaurantsLD.getValue(), listNearbyRestaurantsLD.getValue(), mLastRestaurantFetchVisibleRegionLD.getValue());
        });

        mUiModelLiveData.addSource(listNearbyRestaurantsLD, listNearbyRestaurants -> {
            combineSources(mIsMapLoadedLD.getValue(), lastMapVisibleRegionLD.getValue(), mCurrentGPSLocationLD.getValue(), fireStoreRestaurantsLD.getValue(), listNearbyRestaurants, mLastRestaurantFetchVisibleRegionLD.getValue());
        });
        mUiModelLiveData.addSource(fireStoreRestaurantsLD, fireStoreRestaurants -> {
            combineSources(mIsMapLoadedLD.getValue(), lastMapVisibleRegionLD.getValue(), mCurrentGPSLocationLD.getValue(), fireStoreRestaurants, listNearbyRestaurantsLD.getValue(), mLastRestaurantFetchVisibleRegionLD.getValue());
        });

        mUiModelLiveData.addSource(mLastRestaurantFetchVisibleRegionLD, lastRestaurantFetchVisibleRegion -> {
            combineSources(mIsMapLoadedLD.getValue(), lastMapVisibleRegionLD.getValue(), mCurrentGPSLocationLD.getValue(), fireStoreRestaurantsLD.getValue(), listNearbyRestaurantsLD.getValue(), lastRestaurantFetchVisibleRegion);
        });
    }

    private void wireUpNearbyParams() {
        mNearbyParamsMLD.addSource(mCurrentGPSLocationLD, currentGPSLocation -> {
            combineNearbyParamsSources(currentGPSLocation, mVisibleRegionRepo.getLastNearbyRestaurantsFetchVisibleRegion().getValue());
        });
        mNearbyParamsMLD.addSource(mVisibleRegionRepo.getLastNearbyRestaurantsFetchVisibleRegion(), lastNearbyFetchVisibleRegion -> {
            combineNearbyParamsSources(mCurrentGPSLocationLD.getValue(), lastNearbyFetchVisibleRegion);
        });
    }

    private void combineNearbyParamsSources(Location currentGPSLocation, VisibleRegion lastRestaurantFetchVisibleRegion) {
        if (lastRestaurantFetchVisibleRegion != null) {
            String location = fromLatLngToLocationString(lastRestaurantFetchVisibleRegion.latLngBounds.getCenter());
            Integer radius = fromVisibleRegionToFetchRadius(lastRestaurantFetchVisibleRegion);
            mNearbyParamsMLD.setValue(new Pair<>(location, radius));
        } else if (currentGPSLocation != null) {
            String location = fromLocationToLocationString(currentGPSLocation);
            Integer radius = 500;
            mNearbyParamsMLD.setValue(new Pair<>(location, radius));
        } else {
            mNearbyParamsMLD.setValue(null);
        }
    }

    private void combineSources(
            @Nullable Boolean isMapReady,
            @Nullable VisibleRegion lastVisibleRegion,
            @Nullable Location currentGPSlocation,
            @Nullable List<FireStoreRestaurant> fireStoreRestaurants,
            @Nullable List<NearbyPlace> nearbyPlaces,
            @Nullable VisibleRegion lastRestaurantFetchVisibleRegion
    ) {
        if (isMapReady != null && isMapReady) {
            LatLng currentGPSLatLng = null;
            LatLngBounds lastLatLngBounds = null;
            boolean searchButtonVisibility = false;
            if (!mCameraHasBeenInitializedToLastPosition) {
                if (lastVisibleRegion != null) {
                    lastLatLngBounds = lastVisibleRegion.latLngBounds;
                } else if (currentGPSlocation != null) {
                    currentGPSLatLng = new LatLng(currentGPSlocation.getLatitude(), currentGPSlocation.getLongitude());
                }
            }

            if (lastVisibleRegion != null && lastRestaurantFetchVisibleRegion != null) {
                searchButtonVisibility = shouldNewFetchButtonBeVisible(lastVisibleRegion, lastRestaurantFetchVisibleRegion);
            }
            List<CustomMapMarker> listMapMarkers = createMarkers(nearbyPlaces, fireStoreRestaurants);
            mUiModelLiveData.setValue(new MapFragmentModel(
                    listMapMarkers,
                    lastLatLngBounds,
                    searchButtonVisibility,
                    mPermissionService.isLocationPermissionGranted(),
                    currentGPSLatLng));
        }
    }

    private boolean shouldNewFetchButtonBeVisible(VisibleRegion lastVisibleRegion, VisibleRegion lastRestaurantFetchVisibleRegion) {
        Location lastVisibleLocation = fromLatLngToLocation(lastVisibleRegion.latLngBounds.getCenter());
        Location lastFetchLocation = fromLatLngToLocation(lastRestaurantFetchVisibleRegion.latLngBounds.getCenter());
        Location lastVisibleNearLeftCornerLocation = fromLatLngToLocation(lastVisibleRegion.nearLeft);
        Location lastFetchNearLeftCornerLocation = fromLatLngToLocation(lastRestaurantFetchVisibleRegion.nearLeft);
        Location lastVisibleNearRightCornerLocation = fromLatLngToLocation(lastVisibleRegion.nearRight);


        if (lastVisibleNearLeftCornerLocation.distanceTo(lastVisibleNearRightCornerLocation) > 5000) {
            return false;
        }
        if (lastFetchLocation.distanceTo(lastVisibleLocation) > 200) {
            return true;
        }
        if (lastVisibleNearLeftCornerLocation.distanceTo(lastFetchNearLeftCornerLocation) > 200) {
            return true;
        }
        return false;
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


    public void setLastVisibleRegion(VisibleRegion visibleRegion) {
        if (!mCameraHasBeenInitializedToLastPosition) {
            mCameraHasBeenInitializedToLastPosition = true;
        }
        if (mLastRestaurantFetchVisibleRegionLD.getValue() == null) {
            mVisibleRegionRepo.setLastNearbyRestaurantsFetchVisibleRegion(visibleRegion);
        }
        mVisibleRegionRepo.setLastMapVisibleRegion(visibleRegion);
    }

    public void onResume() {
        mCurrentGPSLocationRepo.refresh();
    }

    public void mapIsReady() {
        mIsMapLoadedLD.setValue(true);
    }

    public LiveData<MapFragmentModel> getUIModel() {
        return mUiModelLiveData;
    }

    private String fromLocationToLocationString(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    private int fromVisibleRegionToFetchRadius(VisibleRegion lastNearbySearchFetchVisibleRegion) {
        Location nearRightLocation = fromLatLngToLocation(lastNearbySearchFetchVisibleRegion.nearRight);
        Location nearLeftLocation = fromLatLngToLocation(lastNearbySearchFetchVisibleRegion.nearLeft);
        //Giving room to radius to fit the all icon
        return (int) ((nearLeftLocation.distanceTo(nearRightLocation) / 2) * 0.8);
    }

    private Location fromLatLngToLocation(LatLng latlng) {
        Location location = new Location("");
        location.setLatitude(latlng.latitude);
        location.setLongitude(latlng.longitude);
        return location;
    }

    private String fromLatLngToLocationString(LatLng center) {
        return center.latitude + "," + center.longitude;
    }

    public void setLastFetchRestaurantVisibleRegion(VisibleRegion visibleRegion) {
        mVisibleRegionRepo.setLastNearbyRestaurantsFetchVisibleRegion(visibleRegion);
    }
}
