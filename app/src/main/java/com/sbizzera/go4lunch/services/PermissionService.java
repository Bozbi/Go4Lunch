package com.sbizzera.go4lunch.services;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.PendingResult;
import com.sbizzera.go4lunch.App;

import timber.log.Timber;

public class PermissionService{

    private static PermissionService sPermissionService;
    private MutableLiveData<Boolean> mFineLocationPermission = new MutableLiveData<>();

    public PermissionService() {
        checkPermission();
    }

    public static PermissionService getInstance() {
        if (sPermissionService == null) {
            sPermissionService = new PermissionService();
        }
        return sPermissionService;
    }

    public LiveData<Boolean> getPermissionLiveData() {
        return mFineLocationPermission;
    }

    public void checkPermission() {
        Boolean fineLocalPermission = ContextCompat.checkSelfPermission(App.getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Timber.d("Permission is set to %s", fineLocalPermission);
        mFineLocationPermission.postValue(fineLocalPermission);
    }

}
