package com.sbizzera.go4lunch.model;

import java.util.List;

public class FireStoreUser {

    private String userId;
    private List<String> likedRestaurantIdList;

    public FireStoreUser() {
    }

    public FireStoreUser(String userId, List<String> likedRestaurantIdList) {
        this.userId = userId;
        this.likedRestaurantIdList = likedRestaurantIdList;
    }

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}

    public List<String> getLikedRestaurantList() {return likedRestaurantIdList;}

    public void setLikedRestaurantList(List<String> likedRestaurantIdList) {this.likedRestaurantIdList = likedRestaurantIdList;}
}
