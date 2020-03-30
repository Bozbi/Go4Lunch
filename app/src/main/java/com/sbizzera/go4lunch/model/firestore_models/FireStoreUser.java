package com.sbizzera.go4lunch.model.firestore_models;

import androidx.annotation.NonNull;

public class FireStoreUser {

    private String userId;
    private String userName;
    private String userAvatarUrl;

    public FireStoreUser() {
    }

    public FireStoreUser(String userId, String userName, String userAvatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @NonNull
    @Override
    public String toString() {
        return "User: " + userName;
    }
}
