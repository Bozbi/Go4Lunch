package com.sbizzera.go4lunch.services;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

}
