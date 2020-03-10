package com.sbizzera.go4lunch;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PermissionHandler {

    private static PermissionHandler sPermissionHandler;
    private MutableLiveData<Boolean> mFineLocationPermission = new MutableLiveData<>();

    public PermissionHandler() {
        checkLocationPermission(App.getApplication());
    }

    public static PermissionHandler getInstance() {
        if (sPermissionHandler == null) {
            sPermissionHandler = new PermissionHandler();
        }
        return sPermissionHandler;
    }

    public void checkLocationPermission(Application application) {
        mFineLocationPermission.postValue(ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public LiveData<Boolean> getPermissionLiveData() {
        return mFineLocationPermission;
    }


}
