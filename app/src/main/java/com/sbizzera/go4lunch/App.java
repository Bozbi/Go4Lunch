package com.sbizzera.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sbizzera.go4lunch.services.NotificationWorker;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

        //TODO Need to put this in a class WorkManagerHelper
        getWorkManager();

        createNotificationChannels();
    }

    private void getWorkManager() {
        Calendar currentDate = Calendar.getInstance();
//        Calendar dueDate = Calendar.getInstance();
//        dueDate.set(Calendar.HOUR_OF_DAY, 16);
//        dueDate.set(Calendar.MINUTE, 59);
//        dueDate.set(Calendar.SECOND, 0);

//        if (dueDate.before(currentDate)) {
//            dueDate.add(Calendar.HOUR_OF_DAY, 24);
//        }

//        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
        long timeDiff = currentDate.getTimeInMillis()+30000 - currentDate.getTimeInMillis();

        OneTimeWorkRequest dailyWorkRequest =
                new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .addTag(TAG_DAILY_WORK)
                        .build();

        // Clear every work that has been programmed in NotificationWorker itself (avoiding doubles)
        WorkManager.getInstance(this).pruneWork();

        // Enqueue notification work
        //TODO add if user ok and Toggle on or off on settings click
        WorkManager.getInstance(this).enqueue(dailyWorkRequest);
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

