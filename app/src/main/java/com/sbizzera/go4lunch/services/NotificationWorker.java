package com.sbizzera.go4lunch.services;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;

import org.threeten.bp.LocalDate;

import timber.log.Timber;

public class NotificationWorker extends ListenableWorker {

    FireStoreService service = new FireStoreService();

    public NotificationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer ->
                FirebaseFirestore.getInstance().collection("dates")
               .document(LocalDate.now().toString())
               .collection("lunches")
               .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
               .get()
                .addOnFailureListener(completer::setException)
                .addOnSuccessListener(documentSnapshot -> {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_USER_LUNCH_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Todays Lunch")
//                            .setContentText(documentSnapshot.get("restaurantName").toString())
                            .build();

                    notificationManager.notify(1, notification);
                    Timber.d(documentSnapshot.get("restaurantName").toString());
;
                    completer.set(Result.success());
                }));
    }


}
