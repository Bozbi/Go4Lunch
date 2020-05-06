package com.sbizzera.go4lunch.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.firebase.ui.auth.AuthUI;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.services.AuthService;


import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class NotificationWorker extends ListenableWorker {

    public NotificationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        //TODO work again on this part
        WorkManagerHelper workManagerHelper = WorkManagerHelper.getInstance(App.getApplication());
        workManagerHelper.handleNotificationWork();
        AuthService authService = AuthService.getInstance(FirebaseAuth.getInstance(), AuthUI.getInstance());
        return CallbackToFutureAdapter.getFuture(completer -> {
            if (authService.getUser() != null) {
                return FirebaseFirestore.getInstance().collection("dates")
                        .document(LocalDate.now().toString())
                        .collection("lunches")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get()
                        .addOnFailureListener(completer::setException)
                        .addOnSuccessListener(userLunch -> {
                            if (userLunch.exists()) {
                                //user is logged and made a choice
                                String restaurantChosen = userLunch.get("restaurantName").toString();
                                FirebaseFirestore.getInstance().collection("dates")
                                        .document(LocalDate.now().toString())
                                        .collection("lunches")
                                        .whereEqualTo("restaurantId", userLunch.get("restaurantId"))
                                        .get()
                                        .addOnSuccessListener(allLunchesForGivenRestaurant -> {
                                            if (allLunchesForGivenRestaurant != null) {
                                                List<String> joiningWorkmatesNames = new ArrayList<>();
                                                for (int i = 0; i < allLunchesForGivenRestaurant.size(); i++) {
                                                    String userNameToJoin = allLunchesForGivenRestaurant.getDocuments().get(i).get("userName").toString();
                                                    if (!userNameToJoin.equals(authService.getUser().getDisplayName())) {
                                                        joiningWorkmatesNames.add(userNameToJoin);
                                                    }
                                                }
                                                Timber.d("List of joining Mates: %s", joiningWorkmatesNames.toString());
                                                if (joiningWorkmatesNames.size() > 0) {
                                                    NotificationHelper.notifyLunchChoice(restaurantChosen, joiningWorkmatesNames);
                                                } else {
                                                    NotificationHelper.notifyLunchChoice(restaurantChosen);
                                                }
                                            }
                                        });
                            } else {
                                //user is logged but didn't make a choice
                                NotificationHelper.notifyLunchChoice();
                            }

                            completer.set(Result.success());
                        });
            }
            // user not Logged In
            return completer.set(Result.success());
        });
    }
}





