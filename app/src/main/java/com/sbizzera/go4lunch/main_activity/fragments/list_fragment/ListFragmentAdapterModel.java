package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import androidx.annotation.ColorRes;
import androidx.annotation.IntegerRes;

public class ListFragmentAdapterModel {
    @IntegerRes
    private int metersTextVisibility;
    private String restaurantName;
    private String restaurantId;
    private String restaurantAddress;
    private String openHoursText;
    @ColorRes
    private int openHoursTextColor;
    private String distance;
    private String workmatesLunchesCount;
    @IntegerRes
    private int star1Visibility;
    @IntegerRes
    private int star2Visibility;
    @IntegerRes
    private int star3Visibility;
    private String photoUrl;

    public ListFragmentAdapterModel(String restaurantName, String restaurantId, String restaurantAddress, String openHoursText, int openHoursTextColor, String distance, int metersTextVisibility, String workmatesLunchesCount, int star1Visibility, int star2Visibility, int star3Visibility, String photoUrl) {
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.restaurantAddress = restaurantAddress;
        this.openHoursText = openHoursText;
        this.openHoursTextColor = openHoursTextColor;
        this.distance = distance;
        this.metersTextVisibility = metersTextVisibility;
        this.workmatesLunchesCount = workmatesLunchesCount;
        this.star1Visibility = star1Visibility;
        this.star2Visibility = star2Visibility;
        this.star3Visibility = star3Visibility;
        this.photoUrl = photoUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getOpenHoursText() {
        return openHoursText;
    }

    public int getOpenHoursTextColor() {
        return openHoursTextColor;
    }

    public String getDistance() {
        return distance;
    }

    public int getMetersTextVisibility() {
        return metersTextVisibility;
    }

    public String getWorkmatesLunchesCount() {
        return workmatesLunchesCount;
    }

    public int getStar1Visibility() {
        return star1Visibility;
    }

    public int getStar2Visibility() {
        return star2Visibility;
    }

    public int getStar3Visibility() {
        return star3Visibility;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }


}
