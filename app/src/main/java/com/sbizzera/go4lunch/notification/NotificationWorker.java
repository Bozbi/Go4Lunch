package com.sbizzera.go4lunch.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.repositories.SharedPreferencesRepo;
import com.sbizzera.go4lunch.services.AuthHelper;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        AuthHelper authHelper = AuthHelper.getInstance(FirebaseAuth.getInstance(), AuthUI.getInstance());
        SharedPreferencesRepo sharedPreferences = SharedPreferencesRepo.getInstance(App.getApplication());
        if (authHelper.getUser() != null && sharedPreferences.isNotificationPrefOn(authHelper.getUser().getUid())) {
            FirebaseFirestore.getInstance().collection("dates")
                    .document(LocalDate.now().toString())
                    .collection("lunches")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
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
                                                if (!userNameToJoin.equals(authHelper.getUser().getDisplayName())) {
                                                    joiningWorkmatesNames.add(userNameToJoin);
                                                }
                                            }
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

                    });
        }
        WorkManagerHelper.getInstance(App.getApplication()).enqueueWork();
        return Result.success();
    }
}
