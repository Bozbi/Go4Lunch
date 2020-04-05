package com.sbizzera.go4lunch.notification;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.services.FirebaseAuthService;

import timber.log.Timber;

public class SharedPreferencesRepo {

    private static final String NOTIFICATION_STATUS= "NOTIFICATION_STATUS";
    private MutableLiveData<Boolean> notificationPreferencesLD = new MutableLiveData<>();

    public SharedPreferencesRepo(){
        updateLiveData();
    }

    public  void saveNotificationPreferences (Boolean bol){
        Timber.d("in Here");
        SharedPreferences sharedPreferences = App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_STATUS,bol);
        editor.apply();
        updateLiveData();
        WorkManagerHelper.handleNotificationWork();
    }

    public void updateLiveData(){
        notificationPreferencesLD.setValue(App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(),Context.MODE_PRIVATE).getBoolean(NOTIFICATION_STATUS,true));
        Timber.d("NotificationOn : %s",notificationPreferencesLD.getValue());
    }


    public LiveData<Boolean> getNotificationPreferencesLiveData(){
        return notificationPreferencesLD;
    }

    public static Boolean isNotificationPrefOn(){
        SharedPreferences sharedPreferences = App.getApplication().getSharedPreferences(FirebaseAuthService.getUser().getUid(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION_STATUS,true);
    }
}
