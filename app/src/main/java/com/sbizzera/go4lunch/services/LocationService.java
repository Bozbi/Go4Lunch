package com.sbizzera.go4lunch.services;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import timber.log.Timber;


public class LocationService {

    private Context mContext;

    public LocationService(Context context) {
        mContext = context;
    }

    public LiveData<Location> getLocation(){
        MutableLiveData<Location> locationLD = new MutableLiveData<>();
        LocationServices.getFusedLocationProviderClient(mContext).getLastLocation().addOnSuccessListener(location -> {
            locationLD.postValue(location);
            Timber.d("getLocation : %s", Go4LunchUtils.locationToString(location));
        });
        return locationLD;
    }

}
