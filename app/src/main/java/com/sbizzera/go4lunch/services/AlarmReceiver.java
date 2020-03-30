package com.sbizzera.go4lunch.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sbizzera.go4lunch.App;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("coucou");
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(3, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(App.getApplication()).enqueue(work);
    }
}
