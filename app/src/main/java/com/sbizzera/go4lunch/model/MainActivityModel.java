package com.sbizzera.go4lunch.model;

public class MainActivityModel {

    private String userPhotoUrl;

    private String userName;

    private String userEmail;

    private String toolBarTitle;

    private Boolean isNotificationOn;

    private String switchText;

    public MainActivityModel(String userPhotoUrl, String userName, String userEmail, String toolBarTitle, Boolean isNotificationOn, String switchText) {
        this.userPhotoUrl = userPhotoUrl;
        this.userName = userName;
        this.userEmail = userEmail;
        this.toolBarTitle = toolBarTitle;
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

    public String getToolBarTitle() {
        return toolBarTitle;
    }

    public String getSwitchText() {
        return switchText;
    }
}
