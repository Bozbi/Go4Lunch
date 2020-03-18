package com.sbizzera.go4lunch.view_models;


import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.model.MapFragmentModel;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.services.DeviceLocator;
import com.sbizzera.go4lunch.services.PermissionHandler;
import com.sbizzera.go4lunch.services.RestaurantRepository;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.List;

public class MapFragmentViewModel extends ViewModel {

    private static final String TAG = "MapFragmentViewModel";

    private MediatorLiveData<MapFragmentModel> mUiModelLiveData = new MediatorLiveData<>();
    private DeviceLocator mLocator;
    private PermissionHandler mPermissionHandler;
    private RestaurantRepository mRestaurantRepository;

    private LiveData<Boolean> fineLocationPermissionLiveData;
    private LiveData<Location> locationLiveData;
    private LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData;


     MapFragmentViewModel(DeviceLocator locator, PermissionHandler permissionHandler, RestaurantRepository restaurantRepository) {
        mLocator = locator;
        mPermissionHandler = permissionHandler;
        mRestaurantRepository = restaurantRepository;
        wireUpMediator();

    }

    public LiveData<MapFragmentModel> getUIModel() {
        return mUiModelLiveData;
    }


    public void wireUpMediator() {

        fineLocationPermissionLiveData = mPermissionHandler.getPermissionLiveData();
        locationLiveData = mLocator.getLocation();
        nearbyRestaurantsLiveData = Transformations.switchMap(locationLiveData, new Function<Location, LiveData<List<NearbyPlace>>>() {
            @Override
            public LiveData<List<NearbyPlace>> apply(Location location) {
                return mRestaurantRepository.getNearbyRestaurants(Go4LunchUtils.locationToString(location));
            }
        });

        mUiModelLiveData.addSource(fineLocationPermissionLiveData, fineLocationPermission -> {
            mUiModelLiveData.postValue(combineLocationAndPermission(locationLiveData.getValue(), fineLocationPermission, nearbyRestaurantsLiveData.getValue()));
        });

        mUiModelLiveData.addSource(locationLiveData, location -> {
            mUiModelLiveData.postValue(combineLocationAndPermission(location, fineLocationPermissionLiveData.getValue(), nearbyRestaurantsLiveData.getValue()));
        });

        mUiModelLiveData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> {
            mUiModelLiveData.postValue(combineLocationAndPermission(locationLiveData.getValue(), fineLocationPermissionLiveData.getValue(), nearbyRestaurants));
        });

    }


    private MapFragmentModel combineLocationAndPermission(Location location, Boolean fineLocationPermission, List<NearbyPlace> restaurantslist) {
        MapFragmentModel model = new MapFragmentModel();
        model.setFineLocationPermission(fineLocationPermission);
        model.setLocation(location);
        model.setRestaurantsList(restaurantslist);
        return model;
    }

    public void updatePermissionAndLocation() {
        PermissionHandler.getInstance().checkLocationPermission();
        mLocator.getLocation();
    }


}
