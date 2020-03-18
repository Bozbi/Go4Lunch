package com.sbizzera.go4lunch.model.firestore_database_models;

public class FireStoreUser {

    private String userID;
    private String userName;
    private String userAvatarUrl;

    public FireStoreUser() {
    }

    public FireStoreUser(String userID, String userName, String userAvatarUrl) {
        this.userID = userID;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
