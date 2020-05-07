package com.sbizzera.go4lunch.workmates_fragment.models;

public class WorkmatesFragmentAdapterModel {

    private String photoUrl;

    private String choice;

    private Boolean isClickable;

    private int textStyle;

    private String restaurantId;

    public WorkmatesFragmentAdapterModel(String photoUrl, String choice, Boolean isClickable, int textStyle, String restaurantId) {
        this.photoUrl = photoUrl;
        this.choice = choice;
        this.isClickable = isClickable;
        this.textStyle = textStyle;
        this.restaurantId = restaurantId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public Boolean getClickable() {
        return isClickable;
    }

    public void setClickable(Boolean clickable) {
        isClickable = clickable;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
