package com.sbizzera.go4lunch.model;

public class FireStoreRestaurant {

    private String id;

    private int likeCount;

    public FireStoreRestaurant() {
    }

    public FireStoreRestaurant(String id, int likeCount) {
        this.id = id;
        this.likeCount = likeCount;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public int getLikeCount() {return likeCount;}

    public void setLikeCount(int likeCount) {this.likeCount = likeCount;}
}
