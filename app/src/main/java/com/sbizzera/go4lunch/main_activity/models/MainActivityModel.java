package com.sbizzera.go4lunch.main_activity.models;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.libraries.places.api.model.RectangularBounds;

public class MainActivityModel {

    private String userPhotoUrl;

    private String userName;

    private String userEmail;

    private Boolean isNotificationOn;

    private String switchText;




    public MainActivityModel(String userPhotoUrl, String userName, String userEmail, Boolean isNotificationOn, String switchText) {
        this.userPhotoUrl = userPhotoUrl;
        this.userName = userName;
        this.userEmail = userEmail;
        this.isNotificationOn = isNotificationOn;
        this.switchText = switchText;

    }

    public Boolean getNotificationOn() {
        return isNotificationOn;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getSwitchText() {
        return switchText;

    }

}
