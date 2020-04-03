package com.sbizzera.go4lunch.services;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sbizzera.go4lunch.App;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class WorkManagerHelper {

    private static final String TAG_DAILY_WORK = "TAG_DAILY_WORK";
    private static WorkManager workManager = WorkManager.getInstance(App.getApplication());

    public static void handleNotificationWork() {
        // Clear every work that has been programmed in NotificationWorker itself (avoiding doubles)
        clearAllWork();

        //If User Wants Notification
        if (SharedPreferencesRepo.loadNotificationPreferences()) {
            enqueueWork();
        }
    }

    public static void enqueueWork(){
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

    public static void clearAllWork(){
        workManager.pruneWork();
    }
}
