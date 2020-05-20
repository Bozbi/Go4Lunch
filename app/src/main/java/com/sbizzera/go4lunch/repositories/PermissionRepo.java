package com.sbizzera.go4lunch.repositories;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

import com.sbizzera.go4lunch.App;

public class PermissionRepo {

    private static PermissionRepo sPermissionRepo;

    private Application application;

    private boolean hasPermissionBeenAsked = false;
    
    @VisibleForTesting
    private PermissionRepo(Application application) {
        this.application = application;
    }

    public static PermissionRepo getInstance() {
        if (sPermissionRepo == null) {
            sPermissionRepo = new PermissionRepo(App.getApplication());
        }
        return sPermissionRepo;
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
