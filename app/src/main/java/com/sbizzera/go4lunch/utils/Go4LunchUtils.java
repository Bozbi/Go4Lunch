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

    public static String getUserFirstName(String fullName) {
        String firstName = fullName.substring(0,fullName.indexOf(" "));
        firstName = firstName.substring(0,1).toUpperCase()+ firstName.substring(1);
        return firstName;
    }
}
