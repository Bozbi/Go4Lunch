package com.sbizzera.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sbizzera.go4lunch.App;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkManagerHelper {

    private static final String TAG_DAILY_WORK = "TAG_DAILY_WORK";
    public static final String CHANNEL_USER_LUNCH_ID = "CHANNEL_USER_LUNCH";
    private  WorkManager mWorkManager;
    private static WorkManagerHelper sWorkManagerHelper ;

    private WorkManagerHelper(Context context){
        mWorkManager = WorkManager.getInstance(context);
    }

    public static WorkManagerHelper getInstance(Context context){
        if(sWorkManagerHelper==null){
            sWorkManagerHelper = new WorkManagerHelper(context);
        }
        return sWorkManagerHelper;
    }

    public void handleNotificationWork() {
        // Clear every work that has been programmed in NotificationWorker itself (avoiding doubles)
        clearAllWork();

        enqueueWork();
    }

    private void enqueueWork(){

        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(TAG_DAILY_WORK)
                .build();

        mWorkManager.enqueue(workRequest);
    }

    private void clearAllWork(){
        mWorkManager.cancelAllWorkByTag(TAG_DAILY_WORK);
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelUserLunch = new NotificationChannel(
                    CHANNEL_USER_LUNCH_ID,
                    "Notify Lunch Choice",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelUserLunch.setDescription("Notify you're lunch choice every day ");

            NotificationManager manager = App.getApplication().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelUserLunch);
        }
    }



}
