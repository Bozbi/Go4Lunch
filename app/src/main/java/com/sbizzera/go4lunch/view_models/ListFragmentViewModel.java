package com.sbizzera.go4lunch.view_models;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.model.ListFragmentAdapterModel;
import com.sbizzera.go4lunch.model.ListFragmentModel;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.services.DeviceLocator;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.PermissionHandler;
import com.sbizzera.go4lunch.services.RestaurantRepository;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.List;

public class ListFragmentViewModel extends ViewModel {

    private DeviceLocator locator;
    private PermissionHandler permissionHandler;
    private RestaurantRepository restaurantRepository;
    private FireStoreService fireStoreService;

    private MediatorLiveData<ListFragmentModel> modelLiveData = new MediatorLiveData<>();

    public ListFragmentViewModel(DeviceLocator locator, PermissionHandler permissionHandler, RestaurantRepository restaurantRepository, FireStoreService fireStoreService) {
        this.locator = locator;
        this.permissionHandler = permissionHandler;
        this.restaurantRepository = restaurantRepository;
        this.fireStoreService = fireStoreService;

        wireUpMediator();
    }

    public LiveData<ListFragmentModel> getModel() {
        return modelLiveData;
    }

    private void wireUpMediator() {
        LiveData<Boolean> fineLocationPermissionLiveData = permissionHandler.getPermissionLiveData();
        LiveData<Location> locationLiveData = locator.getLocation();
        LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData = Transformations.switchMap(locationLiveData, location -> restaurantRepository.getNearbyRestaurants(Go4LunchUtils.locationToString(location)));

        modelLiveData.addSource(fineLocationPermissionLiveData, fineLocationPermission -> {
            modelLiveData.postValue(combineSources(locationLiveData.getValue(), fineLocationPermission, nearbyRestaurantsLiveData.getValue()));
        });

        modelLiveData.addSource(locationLiveData, location -> {
            modelLiveData.postValue(combineSources(location, fineLocationPermissionLiveData.getValue(), nearbyRestaurantsLiveData.getValue()));
        });

        modelLiveData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> {
            modelLiveData.postValue(combineSources(locationLiveData.getValue(), fineLocationPermissionLiveData.getValue(), nearbyRestaurants));
        });
    }

    private ListFragmentModel combineSources(Location location,Boolean permission, List<NearbyPlace> nearbyRestaurants){
        ListFragmentModel model = new ListFragmentModel();
        return  model;
    }
}
