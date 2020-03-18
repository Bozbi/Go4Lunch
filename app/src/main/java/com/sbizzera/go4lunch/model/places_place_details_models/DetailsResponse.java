package com.sbizzera.go4lunch.model.places_place_details_models;



import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailsResponse {

    @SerializedName("result")
    private DetailResult detailResult;

    public DetailResult getDetailResult() {
        return detailResult;
    }

    public void setDetailResult(DetailResult detailResult) {
        this.detailResult = detailResult;
    }

    public class DetailResult{
        @SerializedName("name")
        private String name;

        @SerializedName("address_components")
        private List<AddressComponent> addressComponentList;

        @SerializedName("formatted_phone_number")
        private String phoneNumber;

        @SerializedName("website")
        private String webSiteUrl;

        @SerializedName("photos")
        private List<Photos> photosList;

        @SerializedName("place_id")
        private String placeId;

        @SerializedName("opening_hours")
        private OpeningHours openingHours;

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        public void setOpeningHours(OpeningHours openingHours) {
            this.openingHours = openingHours;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AddressComponent> getAddressComponentList() {
            return addressComponentList;
        }

        public void setAddressComponentList(List<AddressComponent> addressComponentList) {
            this.addressComponentList = addressComponentList;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getWebSiteUrl() {
            return webSiteUrl;
        }

        public void setWebSiteUrl(String webSiteUrl) {
            this.webSiteUrl = webSiteUrl;
        }

        public List<Photos> getPhotosList() {
            return photosList;
        }

        public void setPhotosList(List<Photos> photosList) {
            this.photosList = photosList;
        }

        public class Photos{

            @SerializedName("photo_reference")
            private String photoReference;

            public String getPhotoReference() {
                return photoReference;
            }

            public void setPhotoReference(String photoReference) {
                this.photoReference = photoReference;
            }
        }

        public class AddressComponent{
            @SerializedName("long_name")
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public class OpeningHours{
            @SerializedName("open_now")
            private Boolean openNow;

            public Boolean getOpenNow() {
                return openNow;
            }

            public void setOpenNow(Boolean openNow) {
                this.openNow = openNow;
            }
        }


    }
}
