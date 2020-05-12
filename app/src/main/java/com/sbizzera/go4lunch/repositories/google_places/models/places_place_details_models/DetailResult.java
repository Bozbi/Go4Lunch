package com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailResult {

    @SerializedName("name")
    private String name;

    @SerializedName("address_components")
    private List<AddressComponent> addressComponentList;

    @SerializedName("formatted_phone_number")
    private String phoneNumber;

    @SerializedName("photos")
    private List<Photos> photosList;

    @SerializedName("website")
    private String webSiteUrl;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("opening_hours")
    private OpeningHours openingHours;

    @SerializedName("geometry")
    private Geometry geometry;


    public Geometry getGeometry() {
        return geometry;
    }

    public String getPlaceId() {
        return placeId;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public String getName() {
        return name;
    }

    public List<AddressComponent> getAddressComponentList() {
        return addressComponentList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public List<Photos> getPhotosList() {
        return photosList;
    }
}
