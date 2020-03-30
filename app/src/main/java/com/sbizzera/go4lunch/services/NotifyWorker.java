package com.sbizzera.go4lunch.services;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;

import timber.log.Timber;

public class NotifyWorker extends Worker {

    private FireStoreService service = new FireStoreService();
    private LiveData<String> todaysLunchLiveData;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        wireUp();
    }

    private void wireUp(){
        todaysLunchLiveData = service.getUserLunch();
    }

    @NonNull
    @Override
    public Result doWork() {
        return Result.success();
    }

    private void notifyLunch() throws InterruptedException {
        String lunch = todaysLunchLiveData.getValue();
        Timber.d("lunch : %s", lunch);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_USER_LUNCH_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Todays Lunch")
                .setContentText(lunch)
                .build();

        notificationManager.notify(1, notification);

        Timber.d("Doing Work");
    }
}
