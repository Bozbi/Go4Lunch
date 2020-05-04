package com.sbizzera.go4lunch.services;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.App;

public class PermissionService {

    private static PermissionService sPermissionService;

    private Application application;

    private boolean hasPermissionBeenAsked = false;
    
    @VisibleForTesting
    public PermissionService(Application application) {
        this.application = application;
    }

    public static PermissionService getInstance() {
        if (sPermissionService == null) {
            sPermissionService = new PermissionService(App.getApplication());
        }
        return sPermissionService;
    }

    public Boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public Boolean hasPermissionBeenAsked() {
        return hasPermissionBeenAsked;
    }
    
    public void setHasPermissionBeenAsked(boolean asked) {
        hasPermissionBeenAsked = asked;
    }
}
