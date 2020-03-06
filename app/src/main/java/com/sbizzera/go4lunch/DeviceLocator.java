package com.sbizzera.go4lunch;

import android.app.Activity;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import timber.log.Timber;


public class DeviceLocator {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PermissionHandler mPermission;
    private Location mUserLocation;

    public DeviceLocator(Activity activity) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        mPermission = new PermissionHandler();
    }

    public Location getUserLocation(Fragment fragment) {
        Task<Location> locationResult;
        locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(fragment.requireActivity(), new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Timber.d("getUserLocation Success");
                    mUserLocation = task.getResult();
                    String str = "User's Location : " + mUserLocation.getLatitude() + ", " + mUserLocation.getLongitude();
                    Timber.d(str);
                } else {
                    Timber.d("getUserLocation Failure");
                }
            }
        });

        return mUserLocation;

    }

}
