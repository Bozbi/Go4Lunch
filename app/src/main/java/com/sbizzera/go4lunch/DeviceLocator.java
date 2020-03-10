package com.sbizzera.go4lunch;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import timber.log.Timber;


public class DeviceLocator {

    private static final String TAG = "DeviceLocator";

    private MutableLiveData<Location> mLocation = new MutableLiveData<>();
    private Activity mActivity;

    public DeviceLocator(Activity activity) {
        mActivity = activity;
    }

    private void locate() {
        Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(mActivity).getLastLocation();
        locationTask.addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLocation.postValue(task.getResult());
                }
            }
        });
    }

    public LiveData<Location> getLocation() {
        locate();
        return mLocation;
    }

}
