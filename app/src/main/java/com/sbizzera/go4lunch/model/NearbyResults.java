package com.sbizzera.go4lunch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyResults {

    @SerializedName("results")
    @Expose
    private List<NearbyResult> restaurantList;

    public List<NearbyResult> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<NearbyResult> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public class NearbyResult{
        @SerializedName("place_id")
        @Expose
        private String id ;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("geometry")
        private Geometry geometry;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getLat(){
            return geometry.location.lat;
        }

        public float getLng(){
            return geometry.location.lng;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public class Geometry{
            @SerializedName("location")
            @Expose
            private Location location;

            public Location getLocation() {
                return location;
            }

            public void setLocation(Location location) {
                this.location = location;
            }

            public class Location{
                @SerializedName("lat")
                @Expose
                private float lat;

                @SerializedName("lng")
                @Expose
                private float lng;

                public float getLat() {
                    return lat;
                }

                public void setLat(float lat) {
                    this.lat = lat;
                }

                public float getLng() {
                    return lng;
                }

                public void setLng(float lng) {
                    this.lng = lng;
                }
            }
        }
    }
}
