package com.sbizzera.go4lunch.services;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;


public class DeviceLocator {

    private static final String TAG = "DeviceLocator";

    private MutableLiveData<Location> mLocation = new MutableLiveData<>();
    private Context mContext;

    public DeviceLocator(Context context) {
        mContext = context;
        locate();
    }


    public LiveData<Location> getLocation() {
        return mLocation;
    }

    //Location Updates

    public void locate(){
        LocationRequest locationRequest = LocationRequest.create().setFastestInterval(300000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLocation.postValue(locationResult.getLastLocation());
                Log.d(TAG, "onLocationResult: "+locationResult.getLastLocation().getLatitude());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG,"onLocationAvailability : "+ locationAvailability.toString());
            }
        }, Looper.getMainLooper());


    }

}
