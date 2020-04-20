package com.sbizzera.go4lunch.main_activity.your_lunch_dialog;

import java.io.Serializable;

public class YourLunchModel implements Serializable {
    private String dialogtext;
    private boolean positiveAvailable;
    private String restaurantId;

    public YourLunchModel(String dialogtext, boolean positiveAvailable, String restaurantId) {
        this.dialogtext = dialogtext;
        this.positiveAvailable = positiveAvailable;
        this.restaurantId = restaurantId;
    }

    public String getDialogtext() {
        return dialogtext;
    }

    public boolean isPositiveAvailable() {
        return positiveAvailable;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
}
