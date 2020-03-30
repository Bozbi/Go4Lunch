package com.sbizzera.go4lunch.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.App;

import timber.log.Timber;

public class PermissionService {

    private static final String TAG = "PermissionHandler";

    private static PermissionService sPermissionService;
    private MutableLiveData<Boolean> mFineLocationPermission = new MutableLiveData<>();

    public PermissionService() {
    }

    public static PermissionService getInstance() {
        if (sPermissionService == null) {
            sPermissionService = new PermissionService();
        }
        sPermissionService.checkLocationPermission();
        return sPermissionService;
    }

    public void checkLocationPermission() {
        Boolean fineLocalPermission = ContextCompat.checkSelfPermission(App.getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Timber.d("Permission is set to %s",fineLocalPermission);
        mFineLocationPermission.postValue(fineLocalPermission);
    }

    public LiveData<Boolean> getPermissionLiveData() {
        return mFineLocationPermission;
    }

}
