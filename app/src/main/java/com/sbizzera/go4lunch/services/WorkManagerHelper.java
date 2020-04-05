package com.sbizzera.go4lunch.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sbizzera.go4lunch.App;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class WorkManagerHelper {

    private static final String TAG_DAILY_WORK = "TAG_DAILY_WORK";
    public static final String CHANNEL_USER_LUNCH_ID = "CHANNEL_USER_LUNCH";
    private static WorkManager workManager = WorkManager.getInstance(App.getApplication());

    public static void handleNotificationWork() {
        // Clear every work that has been programmed in NotificationWorker itself (avoiding doubles)
        clearAllWork();

        //If User Wants Notification
        if (SharedPreferencesRepo.isNotificationPrefOn()) {
            enqueueWork();
        }
    }

    private static void enqueueWork(){
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        Timber.d("enqueuing work for %s", dueDate.getTime().toString());
        //TODO next line for testing purposes
//        timeDiff = 10000;

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(TAG_DAILY_WORK)
                .build();

        workManager.enqueue(workRequest);
    }

    private static void clearAllWork(){
        Timber.d("Clearing all work");
        workManager.cancelAllWorkByTag(TAG_DAILY_WORK);
    }

    public static void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelUserLunch = new NotificationChannel(
                    CHANNEL_USER_LUNCH_ID,
                    "Notify Lunch Choice",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelUserLunch.setDescription("Notify you're lunch choice every day ");

            NotificationManager manager = App.getApplication().getSystemService(NotificationManager.class);
            //TODO why this line
            assert manager!=null;
            manager.createNotificationChannel(channelUserLunch);
        }
    }



}
