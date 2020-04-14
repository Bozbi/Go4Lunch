package com.sbizzera.go4lunch.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.main_activity.MainActivity;

import java.util.List;
import java.util.Objects;

class NotificationHelper {

    //TODO Work on notification Text
    //TODO Inject App.context in constructor

    static void notifyLunchChoice(String restaurantName, List<String> joiningWorkmates) {
        String notificationText = "Hi %User%,\nToday you're eating at %Restaurant% with %Workmates%.\nHave a good time!";
        notificationText = notificationText.replace("%Restaurant%", restaurantName);
        StringBuilder workmatesList = new StringBuilder();
        if (joiningWorkmates.size() > 1) {
            for (int i = 0; i < joiningWorkmates.size(); i++) {
                if (i != joiningWorkmates.size() - 1) {
                    workmatesList.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i))).append(", ");
                } else {
                    workmatesList.append("and ").append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i)));
                }
            }
        } else {
            workmatesList.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(0)));
        }
        notificationText = notificationText.replace("%Workmates%", workmatesList.toString());

        createNotification(notificationText);
    }

    static void notifyLunchChoice(String restaurantName) {
        String notificationText = "Hi %User%,\nToday you're eating at %Restaurant%.\nHave a good time!";
        notificationText = notificationText.replace("%Restaurant%", restaurantName);
        createNotification(notificationText);
    }

    static void notifyLunchChoice() {
        String notificationText = "Hi %User%,\nYou have'nt shared your choice today !\nDo it now so workmates can join you !";
        createNotification(notificationText);
    }

    private static void createNotification(String notificationText) {
        PendingIntent contentIntent = PendingIntent.getActivity(App.getApplication(), 0, new Intent(App.getApplication(), MainActivity.class), 0);

        String title = "Your Lunch !";
        String userFirstName = Go4LunchUtils.getUserFirstName(FirebaseAuthService.getUserName());
        notificationText = notificationText.replace("%User%", userFirstName);

        Notification notification = new NotificationCompat.Builder(App.getApplication(), WorkManagerHelper.CHANNEL_USER_LUNCH_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(App.getApplication().getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setChannelId(WorkManagerHelper.CHANNEL_USER_LUNCH_ID)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .build();

        sendNotification(notification);
    }

    private static void sendNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getApplication());
        notificationManager.notify(1, notification);
    }

}
