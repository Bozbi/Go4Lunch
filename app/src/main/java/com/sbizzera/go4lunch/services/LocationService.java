package com.sbizzera.go4lunch.services;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationServices;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import timber.log.Timber;


public class LocationService {

    private MutableLiveData<Location> locationLD = new MutableLiveData<>();
    private static LocationService sLocationService;

    private LocationService(Context context) {
        LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnSuccessListener(location -> {
            Timber.d("last known location : %s",Go4LunchUtils.locationToString(location));
            locationLD.postValue(location);
        });
    }

    public static LocationService getInstance(Context context){
        if(sLocationService == null){
            sLocationService = new LocationService(context);
        }
        return sLocationService;
    }

    public LiveData<Location> getLocation() {
        return locationLD;
    }

}
