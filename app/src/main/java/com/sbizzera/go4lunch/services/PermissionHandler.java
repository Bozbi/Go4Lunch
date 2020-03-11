package com.sbizzera.go4lunch.services;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.App;

public class PermissionHandler {

    private static final String TAG = "PermissionHandler";

    private static PermissionHandler sPermissionHandler;
    private MutableLiveData<Boolean> mFineLocationPermission = new MutableLiveData<>();

    public PermissionHandler() {
    }

    public static PermissionHandler getInstance() {
        if (sPermissionHandler == null) {
            sPermissionHandler = new PermissionHandler();
        }
        sPermissionHandler.checkLocationPermission();
        return sPermissionHandler;
    }

    public void checkLocationPermission() {
        Boolean fineLocalPermission = ContextCompat.checkSelfPermission(App.getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "checkLocationPermission: permission is set to " + fineLocalPermission);
        mFineLocationPermission.postValue(fineLocalPermission);
    }

    public LiveData<Boolean> getPermissionLiveData() {
        return mFineLocationPermission;
    }


}
