package com.sbizzera.go4lunch.repositories;


import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;



public class CurrentGPSLocationRepo {

    private static CurrentGPSLocationRepo sCurrentGPSLocationRepo;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private MutableLiveData<Location> locationLD = new MutableLiveData<>();

    private CurrentGPSLocationRepo(FusedLocationProviderClient fusedLocationProviderClient) {
        mFusedLocationProviderClient = fusedLocationProviderClient;
    }

    public static CurrentGPSLocationRepo getInstance(FusedLocationProviderClient fusedLocationProviderClient){
        if (sCurrentGPSLocationRepo ==null){
            sCurrentGPSLocationRepo = new CurrentGPSLocationRepo(fusedLocationProviderClient);
        }
        return sCurrentGPSLocationRepo;
    }

    public void refresh() {
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            locationLD.postValue(location);
        });
    }

    public LiveData<Location> getCurrentGPSLocationLD() {
        return locationLD;
    }
}
