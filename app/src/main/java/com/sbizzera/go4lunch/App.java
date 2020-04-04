package com.sbizzera.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sbizzera.go4lunch.services.WorkManagerHelper;
import com.sbizzera.go4lunch.views.fragments.MapFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class App extends Application {

    public static final String TAG_DAILY_WORK = "TAG_DAILY_WORK";
    private static Application sApplication;
    public static final String CHANNEL_USER_LUNCH_ID = "CHANNEL_USER_LUNCH";

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

        createNotificationChannels();
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelUserLunch = new NotificationChannel(
                    CHANNEL_USER_LUNCH_ID,
                    "notify for lunch choice",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelUserLunch.setDescription("Notify you're lunch choice every Day at noon");

            NotificationManager manager = getSystemService(NotificationManager.class);
            //TODO why this line
            assert manager!=null;
            manager.createNotificationChannel(channelUserLunch);
        }
    }


    //Returning Application for later user of Context.
    public static Application getApplication() {
        return sApplication;
    }


}

