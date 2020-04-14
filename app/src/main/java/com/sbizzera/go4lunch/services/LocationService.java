package com.sbizzera.go4lunch.services;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationServices;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;

import timber.log.Timber;


public class LocationService {

    private MutableLiveData<Location> locationLD = new MutableLiveData<>();
    private SingleLiveEvent<Location> locationLE = new SingleLiveEvent<>();

    public LocationService(Context context) {
        Timber.d("creating a LocationService");
        LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnSuccessListener(location -> {
            Timber.d("last known location : %s", Go4LunchUtils.locationToString(location));
            locationLD.postValue(location);
            locationLE.postValue(location);
        });
    }

    public LiveData<Location> getLocationLD() {
        return locationLD;
    }

    public SingleLiveEvent<Location> getLocationLE() {
        return locationLE;
    }

}
