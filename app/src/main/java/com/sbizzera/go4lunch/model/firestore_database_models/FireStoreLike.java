package com.sbizzera.go4lunch.model.firestore_database_models;

public class FireStoreLike {
    private String userID;
    private String restaurantID;

    public FireStoreLike() {
    }

    public FireStoreLike(String userID, String restaurantID) {
        this.userID = userID;
        this.restaurantID = restaurantID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }
}
