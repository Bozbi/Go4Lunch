package com.sbizzera.go4lunch.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.App;

public class SharedPreferencesRepo {

    private static SharedPreferencesRepo sSharedPreferencesRepo;

    private static final String NOTIFICATION_STATUS = "NOTIFICATION_STATUS";
    private MutableLiveData<Boolean> notificationPreferencesLD = new MutableLiveData<>();
    private Application mApplication;

    private SharedPreferencesRepo(Application application) {
        mApplication = application;
    }

    public static SharedPreferencesRepo getInstance(Application application) {
        if (sSharedPreferencesRepo == null) {
            sSharedPreferencesRepo = new SharedPreferencesRepo(application);
        }
        return sSharedPreferencesRepo;
    }

    public void saveNotificationPreferences(Boolean bol, String currentUserId) {
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(currentUserId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_STATUS, bol);
        editor.apply();
        updateLiveData(currentUserId);

    }

    public void updateLiveData(String currentUserId) {
        notificationPreferencesLD.setValue(mApplication.getSharedPreferences(currentUserId, Context.MODE_PRIVATE).getBoolean(NOTIFICATION_STATUS, true));
    }


    public LiveData<Boolean> getNotificationPreferencesLiveData() {
        return notificationPreferencesLD;
    }

    public Boolean isNotificationPrefOn(String currentUserId) {
        SharedPreferences sharedPreferences = App.getApplication().getSharedPreferences(currentUserId, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION_STATUS, true);
    }
}
