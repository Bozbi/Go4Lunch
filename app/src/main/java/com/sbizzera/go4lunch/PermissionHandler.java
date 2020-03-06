package com.sbizzera.go4lunch;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import timber.log.Timber;

public class PermissionHandler {

    private static final int REQUEST_ACCESS_FINE_LOCATION_CODE = 1;

    //Check if app has permission to access users location
    public boolean checkFineLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Timber.d("ACCES_FINE_LOCATION permission granted");
            return true;
        }
        Timber.d("ACCESS_FINE_LOCATION permission denied");
        return false;
    }

    //Request permission to access users location
    public void requestFineLocationPermission(Fragment fragment){
        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ACCESS_FINE_LOCATION_CODE);
    }
}
