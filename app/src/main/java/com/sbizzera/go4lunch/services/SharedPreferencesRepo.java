package com.sbizzera.go4lunch.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.sbizzera.go4lunch.App;

import timber.log.Timber;

public class SharedPreferencesRepo {

    private static final String NOTIFICATION_STATUS= "NOTIFICATION_STATUS";


    // Inverting Bol for UI personal preference matter
    public static void saveNotificationPreferences (Boolean bol){
        SharedPreferences sharedPreferences = App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_STATUS,!bol);
        editor.apply();
    }

    public static Boolean loadNotificationPreferences (){
        SharedPreferences sharedPreferences = App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE);
        return !sharedPreferences.getBoolean(NOTIFICATION_STATUS,true);
    }
}
