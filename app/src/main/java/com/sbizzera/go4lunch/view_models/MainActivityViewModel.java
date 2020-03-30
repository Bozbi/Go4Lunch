package com.sbizzera.go4lunch.view_models;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.services.PermissionService;

public class MainActivityViewModel extends ViewModel {

    private FireStoreService fireStore;
    private PermissionService permissionService;

    private LiveData<Boolean> isLocationPermissionOnLD = new MutableLiveData<>();

    MainActivityViewModel(FireStoreService fireStore, PermissionService permissionService, LocationService locationService) {
        this.fireStore = fireStore;
        this.permissionService = permissionService;
        wireUp();
    }

    private void wireUp(){
        isLocationPermissionOnLD = permissionService.getPermissionLiveData();
    }

    public LiveData<Boolean> isLocationPermissionOn(){
        return isLocationPermissionOnLD;
    }

    public void updateUserInDb() {
        fireStore.updateUserInDb();
    }
}
