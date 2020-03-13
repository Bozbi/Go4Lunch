package com.sbizzera.go4lunch.services;

import android.app.Activity;
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
    private Activity mActivity;

    public DeviceLocator(Activity activity) {
        mActivity = activity;
        locateWithUpdate();
    }

    private void locate() {
        Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(mActivity).getLastLocation();
        locationTask.addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(TAG, "Location found :" + Go4LunchUtils.locationToString(location));
                    mLocation.postValue(location);
                } else {
                    Log.d(TAG, "Location not found");
                }
            }
        });
    }

    public LiveData<Location> getLocation() {
        locate();
        return mLocation;
    }

    //Location Updates

    public void locateWithUpdate(){
        LocationRequest locationRequest = LocationRequest.create().setFastestInterval(30000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.getFusedLocationProviderClient(mActivity).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: "+locationResult.getLastLocation().getLatitude());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG,"onLocationAvailability : "+ locationAvailability.toString());
            }
        }, Looper.getMainLooper());


    }

}
