package com.sbizzera.go4lunch.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;

import java.util.List;

public class RestaurantDetailModel {

    private String photoUrl;
    @DrawableRes
    private int fabIcon;
    @ColorRes
    private int fabColor;
    private String restaurantName;
    private String addressText;
    @IntegerRes
    private int star1Visibility;
    @IntegerRes
    private int star2Visibility;
    @IntegerRes
    private int star3Visibility;
    private String phoneNumber;
    @ColorRes
    private int phoneColor;
    private Boolean isPhoneClickable;
    @DrawableRes
    private int likeIcon;
    private String webSiteUrl;
    @ColorRes
    private int webSiteColor;
    private Boolean isWebSiteClickable;
    private List<RestaurantDetailAdapterModel> workmatesList;


    public RestaurantDetailModel(String photoUrl, int fabIcon, int fabColor, String restaurantName, String addressText, int star1Visibility, int star2Visibility, int star3Visibility, String phoneNumber, int phoneColor, Boolean isPhoneClickable, int likeIcon, String webSiteUrl, int webSiteColor, Boolean isWebSiteClickable, List<RestaurantDetailAdapterModel> workmatesList) {
        this.photoUrl = photoUrl;
        this.fabIcon = fabIcon;
        this.fabColor = fabColor;
        this.restaurantName = restaurantName;
        this.addressText = addressText;
        this.star1Visibility = star1Visibility;
        this.star2Visibility = star2Visibility;
        this.star3Visibility = star3Visibility;
        this.phoneNumber = phoneNumber;
        this.phoneColor = phoneColor;
        this.isPhoneClickable = isPhoneClickable;
        this.likeIcon = likeIcon;
        this.webSiteUrl = webSiteUrl;
        this.webSiteColor = webSiteColor;
        this.isWebSiteClickable = isWebSiteClickable;
        this.workmatesList = workmatesList;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getFabIcon() {
        return fabIcon;
    }

    public void setFabIcon(int fabIcon) {
        this.fabIcon = fabIcon;
    }

    public int getFabColor() {
        return fabColor;
    }

    public void setFabColor(int fabColor) {
        this.fabColor = fabColor;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPhoneColor() {
        return phoneColor;
    }

    public void setPhoneColor(int phoneColor) {
        this.phoneColor = phoneColor;
    }

    public Boolean getPhoneClickable() {
        return isPhoneClickable;
    }

    public void setPhoneClickable(Boolean phoneClickable) {
        isPhoneClickable = phoneClickable;
    }

    public int getLikeIcon() {
        return likeIcon;
    }

    public void setLikeIcon(int likeIcon) {
        this.likeIcon = likeIcon;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public int getWebSiteColor() {
        return webSiteColor;
    }

    public void setWebSiteColor(int webSiteColor) {
        this.webSiteColor = webSiteColor;
    }

    public Boolean getWebSiteClickable() {
        return isWebSiteClickable;
    }

    public void setWebSiteClickable(Boolean webSiteClickable) {
        isWebSiteClickable = webSiteClickable;
    }

    public List<RestaurantDetailAdapterModel> getWorkmatesList() {
        return workmatesList;
    }

    public void setWorkmatesList(List<RestaurantDetailAdapterModel> workmatesList) {
        this.workmatesList = workmatesList;
    }
}

