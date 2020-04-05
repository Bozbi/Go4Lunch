package com.sbizzera.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sbizzera.go4lunch.services.WorkManagerHelper;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class App extends Application {


    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        //Initializing ABP
        AndroidThreeTen.init(this);

        //Initializing Timber
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, @NotNull String message, Throwable t) {
                super.log(priority, "OWN TAGS || " + tag, message, t);
            }
        });

       WorkManagerHelper.createNotificationChannels();
    }

    //Returning Application for later user of Context.
    public static Application getApplication() {
        return sApplication;
    }




}

