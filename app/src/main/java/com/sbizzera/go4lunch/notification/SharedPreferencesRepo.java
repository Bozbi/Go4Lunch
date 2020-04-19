package com.sbizzera.go4lunch.notification;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.services.FirebaseAuthService;

public class SharedPreferencesRepo {

    private static SharedPreferencesRepo sSharedPreferencesRepo;

    private static final String NOTIFICATION_STATUS = "NOTIFICATION_STATUS";
    private MutableLiveData<Boolean> notificationPreferencesLD = new MutableLiveData<>();
    private Application mApplication;

    private SharedPreferencesRepo(Application application) {
        mApplication = application;
        updateLiveData();
    }

    public static SharedPreferencesRepo getInstance(Application application){
        if(sSharedPreferencesRepo ==null){
            sSharedPreferencesRepo = new SharedPreferencesRepo(application);
        }
        return sSharedPreferencesRepo;
    }

    public void saveNotificationPreferences(Boolean bol) {
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_STATUS, bol);
        editor.apply();
        updateLiveData();
        WorkManagerHelper.handleNotificationWork();
    }

    private void updateLiveData() {
        notificationPreferencesLD.setValue(App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE).getBoolean(NOTIFICATION_STATUS, true));
    }


    public LiveData<Boolean> getNotificationPreferencesLiveData() {
        return notificationPreferencesLD;
    }

    public static Boolean isNotificationPrefOn() {
        SharedPreferences sharedPreferences = App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION_STATUS, true);
    }
}
