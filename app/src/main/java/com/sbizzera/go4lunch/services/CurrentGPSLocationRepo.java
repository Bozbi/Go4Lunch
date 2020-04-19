package com.sbizzera.go4lunch.services;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationServices;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import timber.log.Timber;


public class CurrentGPSLocationRepo {

    private static CurrentGPSLocationRepo sCurrentGPSLocationRepo;

    private Application mApplication;
    private MutableLiveData<Location> locationLD = new MutableLiveData<>();

    private CurrentGPSLocationRepo(Application application) {
        mApplication = application;
    }

    public static CurrentGPSLocationRepo getInstance(Application application){
        if (sCurrentGPSLocationRepo ==null){
            sCurrentGPSLocationRepo = new CurrentGPSLocationRepo(application);
        }
        return sCurrentGPSLocationRepo;
    }

    public void refresh() {
        LocationServices.getFusedLocationProviderClient(mApplication).getLastLocation().addOnSuccessListener(location -> {
            locationLD.postValue(location);
        });
    }

    public LiveData<Location> getCurrentGPSLocationLD() {
        return locationLD;
    }
}
