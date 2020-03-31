package com.sbizzera.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

public class App extends Application {

    private static Application sApplication;
    public static final String CHANNEL_USER_LUNCH_ID = "CHANNEL_USER_LUNCH";

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        //Initializing ABP
        AndroidThreeTen.init(this);

        //Initializing Timber
        Timber.plant(new Timber.DebugTree(){
            @Override
            protected void log(int priority, String tag, @NotNull String message, Throwable t) {
                super.log(priority, "OWN TAGS || "+ tag, message, t);
            }
        });

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            NotificationChannel channelUserLunch = new NotificationChannel(
                    CHANNEL_USER_LUNCH_ID,
                    "notify for lunch choice",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelUserLunch.setDescription("Notify you're lunch choice every Day at noon");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelUserLunch);
        }
    }


    //Returning Application for later user of Context.
    public static Application getApplication() {
        return sApplication;
    }







}

