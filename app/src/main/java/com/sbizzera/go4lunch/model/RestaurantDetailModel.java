package com.sbizzera.go4lunch.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import java.util.List;

public class RestaurantDetailModel {

    private String photoUrl;
    @DrawableRes
    private int fabIcon;
    private String restaurantName;
    private String addressText;
    @DrawableRes
    private int star1Icon;
    @DrawableRes
    private int star2Icon;
    @DrawableRes
    private int star3Icon;
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

    public RestaurantDetailModel(String photoUrl, int fabIcon, String restaurantName, String addressText, int star1Icon, int star2Icon, int star3Icon, String phoneNumber, int phoneColor, Boolean isPhoneClickable, int likeIcon, String webSiteUrl, int webSiteColor, Boolean isWebSiteClickable, List<RestaurantDetailAdapterModel> workmatesList) {
        this.photoUrl = photoUrl;
        this.fabIcon = fabIcon;
        this.restaurantName = restaurantName;
        this.addressText = addressText;
        this.star1Icon = star1Icon;
        this.star2Icon = star2Icon;
        this.star3Icon = star3Icon;
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

    public int getStar1Icon() {
        return star1Icon;
    }

    public void setStar1Icon(int star1Icon) {
        this.star1Icon = star1Icon;
    }

    public int getStar2Icon() {
        return star2Icon;
    }

    public void setStar2Icon(int star2Icon) {
        this.star2Icon = star2Icon;
    }

    public int getStar3Icon() {
        return star3Icon;
    }

    public void setStar3Icon(int star3Icon) {
        this.star3Icon = star3Icon;
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

