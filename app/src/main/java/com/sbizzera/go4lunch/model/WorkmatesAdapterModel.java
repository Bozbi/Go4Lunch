package com.sbizzera.go4lunch.model;

public class WorkmatesAdapterModel {

    private String photoUrl;

    private String choice;

    public WorkmatesAdapterModel(String photoUrl, String choice) {
        this.photoUrl = photoUrl;
        this.choice = choice;
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
}
