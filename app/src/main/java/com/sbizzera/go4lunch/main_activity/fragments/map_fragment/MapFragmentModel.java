package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;

import android.location.Location;

import com.sbizzera.go4lunch.main_activity.fragments.map_fragment.CustomMapMarker;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentModel {
    private Location location;
    private Boolean fineLocationPermission;
    private List<NearbyPlace> restaurantsList = new ArrayList<>();
    private List<CustomMapMarker> mapMarkersList = new ArrayList<>();


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getFineLocationPermission() {
        return fineLocationPermission;
    }

    public void setFineLocationPermission(Boolean fineLocationPermission) {
        this.fineLocationPermission = fineLocationPermission;
    }

    public List<NearbyPlace> getRestaurantsList() {
        return restaurantsList;
    }

    public void setRestaurantsList(List<NearbyPlace> restaurantsList) {
        this.restaurantsList = restaurantsList;
    }

    public List<CustomMapMarker> getMapMarkersList() {
        return mapMarkersList;
    }

    public void setMapMarkersList(List<CustomMapMarker> mapMarkersList) {
        this.mapMarkersList = mapMarkersList;
    }

}
