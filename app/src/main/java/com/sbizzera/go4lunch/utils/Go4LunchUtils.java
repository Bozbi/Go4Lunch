package com.sbizzera.go4lunch.utils;

import android.location.Location;
import android.util.Log;

public class Go4LunchUtils {

    private static final String TAG = "G4LunchUtils";

    public static String locationToString(Location location) {
        String str;
        try {
            str = location.getLatitude() + "," + location.getLongitude();
        } catch (Exception e) {
            Log.d(TAG, "locationToString: " + e);
            str = "";
        }
        return str;
    }
}
