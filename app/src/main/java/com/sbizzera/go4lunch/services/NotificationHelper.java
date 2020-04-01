package com.sbizzera.go4lunch.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.utils.BitMapCreator;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.views.activities.MainActivity;

import java.util.List;
import java.util.Objects;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

class NotificationHelper {

    //TODO Work on notification Text

    static void notifyLunchChoice(String restaurantName, List<String> joiningWorkmates) {
        //TODO J'en ai marre de checker ça partout??? Comment on fait ?? (Réponse passe à Kotlin non acceptée)
        String userFirstName = Go4LunchUtils.getUserFirstName(Objects.requireNonNull(FirebaseAuthService.getUser().getDisplayName()));
        StringBuilder notificationText = new StringBuilder("Hi " + userFirstName + ", \nToday you're eating at " + restaurantName + " with ");
        if (joiningWorkmates.size() > 1) {
            for (int i = 0; i < joiningWorkmates.size(); i++) {
                if (i != joiningWorkmates.size() - 1) {
                    notificationText.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i))).append(", ");
                } else {
                    notificationText.append("and ").append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i))).append(".");
                }
            }
        }else{
            notificationText.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(0))).append(".");
        }
        notificationText.append("\nHave a Good Time!");

        createNotification(notificationText.toString());
    }

    static void notifyLunchChoice(String restaurantName) {
        String userFirstName = Go4LunchUtils.getUserFirstName(Objects.requireNonNull(FirebaseAuthService.getUser().getDisplayName()));
        String notificationText ="Hi " + userFirstName + ", \nToday you're eating at " + restaurantName+ ".";
        notificationText = notificationText + "\nHave a Good Time!";
        createNotification(notificationText);
    }

    static void notifyLunchChoice() {
        String userFirstName = Go4LunchUtils.getUserFirstName(Objects.requireNonNull(FirebaseAuthService.getUser().getDisplayName()));
        String notificationText ="Hi " + userFirstName + ", \nYou haven't shared your lunch choice today!\n Do it now so Workmates can join you!";
        createNotification(notificationText);
    }

    private static void createNotification(String notificationText) {
        PendingIntent contentIntent = PendingIntent.getActivity(App.getApplication(),0,new Intent(App.getApplication(),MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(App.getApplication(), App.CHANNEL_USER_LUNCH_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("I'm Hungry!")
                .setContentText(notificationText)
                .setChannelId(App.CHANNEL_USER_LUNCH_ID)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                //TODO on content click listener
//                .setContentIntent()
                .build();

        sendNotification(notification);
    }

    private static void sendNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getApplication());
        notificationManager.notify(1, notification);
    }

}
