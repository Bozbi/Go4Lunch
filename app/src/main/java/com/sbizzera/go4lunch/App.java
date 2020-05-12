package com.sbizzera.go4lunch;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.google.android.libraries.places.api.Places;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        WorkManagerHelper workManagerHelper = WorkManagerHelper.getInstance(this);
        workManagerHelper.createNotificationChannels();
        workManagerHelper.clearAllWork();
        workManagerHelper.enqueueWork();

        Places.initialize(this,BuildConfig.GOOGLE_API_KEY);

    }

    //Returning Application for later user of Context.
    public static Application getApplication() {
        return sApplication;
    }
}

