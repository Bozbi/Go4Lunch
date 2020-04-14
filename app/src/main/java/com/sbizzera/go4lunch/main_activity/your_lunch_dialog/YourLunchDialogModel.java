package com.sbizzera.go4lunch.main_activity.your_lunch_dialog;

class YourLunchDialogModel {
    private String yourLunchText;

    private Boolean clickable;

    private String RestaurantId;


    public YourLunchDialogModel(String yourLunchText, Boolean clickable, String restaurantId) {
        this.yourLunchText = yourLunchText;
        this.clickable = clickable;
        RestaurantId = restaurantId;
    }

    public String getYourLunchText() {
        return yourLunchText;
    }

    public Boolean getClickable() {
        return clickable;
    }

    public String getRestaurantId() {
        return RestaurantId;
    }
}
