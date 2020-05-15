package com.sbizzera.go4lunch.notification;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class WorkManagerHelper {

    private static final String TAG_DAILY_WORK = "TAG_DAILY_WORK";
    static final String CHANNEL_USER_LUNCH_ID = "CHANNEL_USER_LUNCH";
    private  WorkManager mWorkManager;
    private Application mApplication;
    private static WorkManagerHelper sWorkManagerHelper ;

    private WorkManagerHelper(Application application){
        mWorkManager = WorkManager.getInstance(application);
        mApplication =application;
    }

    public static WorkManagerHelper getInstance(Application application){
        if(sWorkManagerHelper==null){
            sWorkManagerHelper = new WorkManagerHelper(application);
        }
        return sWorkManagerHelper;
    }



    public void enqueueWork(){

        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

//        timeDiff = 10000;

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(TAG_DAILY_WORK)
                .build();

        mWorkManager.enqueue(workRequest);
    }

    public void clearAllWork(){
        mWorkManager.cancelAllWorkByTag(TAG_DAILY_WORK);
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelUserLunch = new NotificationChannel(
                    CHANNEL_USER_LUNCH_ID,
                    mApplication.getString(R.string.notif_title_channel_get_lunch_choice),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelUserLunch.setDescription(mApplication.getString(R.string.notif_channel_description));
            channelUserLunch.setImportance(NotificationManager.IMPORTANCE_HIGH);


            NotificationManager manager = mApplication.getSystemService(NotificationManager.class);
            if(manager!=null){
                manager.createNotificationChannel(channelUserLunch);
            }
        }
    }

}


