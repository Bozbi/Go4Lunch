package com.sbizzera.go4lunch.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class Go4LunchUtils {

    public static String getUserFirstName(String fullName) {
        if(fullName.contains(" ")) {
            String firstName = fullName.substring(0, fullName.indexOf(" "));
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            return firstName;
        }else{
            return fullName;
        }
    }

    public static Intent getGoToPermissionIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
