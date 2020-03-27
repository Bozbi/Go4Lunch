package com.sbizzera.go4lunch.model;

import androidx.annotation.AnyRes;
import androidx.annotation.ColorRes;
import androidx.annotation.IntegerRes;

public class ListFragmentAdapterModel {
    private String restaurantName;
    private String restaurantId;
    private String restaurantAddress;
    private String openHoursText;
    @ColorRes
    private int openHoursTextColor;
    private int distance;
    private int workmatesLunchesCount;
    @IntegerRes
    private int star1Visibility;
    @IntegerRes
    private int star2Visibility;
    @IntegerRes
    private int star3Visibility;
    private String photoUrl;

    public ListFragmentAdapterModel(String restaurantName, String restaurantId, String restaurantAddress, String openHoursText, int openHoursTextColor, int distance, int workmatesLunchesCount, int star1Visibility, int star2Visibility, int star3Visibility, String photoUrl) {
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.restaurantAddress = restaurantAddress;
        this.openHoursText = openHoursText;
        this.openHoursTextColor = openHoursTextColor;
        this.distance = distance;
        this.workmatesLunchesCount = workmatesLunchesCount;
        this.star1Visibility = star1Visibility;
        this.star2Visibility = star2Visibility;
        this.star3Visibility = star3Visibility;
        this.photoUrl = photoUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getOpenHoursText() {
        return openHoursText;
    }

    public void setOpenHoursText(String openHoursText) {
        this.openHoursText = openHoursText;
    }

    public int getOpenHoursTextColor() {
        return openHoursTextColor;
    }

    public void setOpenHoursTextColor(int openHoursTextColor) {
        this.openHoursTextColor = openHoursTextColor;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getWorkmatesLunchesCount() {
        return workmatesLunchesCount;
    }

    public void setWorkmatesLunchesCount(int workmatesLunchesCount) {
        this.workmatesLunchesCount = workmatesLunchesCount;
    }

    public int getStar1Visibility() {
        return star1Visibility;
    }

    public void setStar1Visibility(int star1Visibility) {
        this.star1Visibility = star1Visibility;
    }

    public int getStar2Visibility() {
        return star2Visibility;
    }

    public void setStar2Visibility(int star2Visibility) {
        this.star2Visibility = star2Visibility;
    }

    public int getStar3Visibility() {
        return star3Visibility;
    }

    public void setStar3Visibility(int star3Visibility) {
        this.star3Visibility = star3Visibility;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
