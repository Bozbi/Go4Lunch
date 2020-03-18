package com.sbizzera.go4lunch.model.firestore_database_models;


import org.threeten.bp.LocalDate;

public class FireStoreUserDailyChoice {

    private String date;
    private String userId;
    private String restaurantId;

    public FireStoreUserDailyChoice() {
    }

    public FireStoreUserDailyChoice(String date, String userId, String restaurantId) {
        this.date = date;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
