package com.sbizzera.go4lunch.model;

import android.location.Location;

import androidx.annotation.NonNull;

import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.views.fragments.MapFragment;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentModel {
    private Location mLocation;
    private Boolean mFineLocationPermission;
    private List<NearbyPlace> mRestaurantsList = new ArrayList<>();

    public Location getLocation (){return mLocation;}

    public void setLocation (Location location){mLocation = location;}

    public List<NearbyPlace> getRestaurantsList() {
        return mRestaurantsList;
    }

    public void setRestaurantsList(List<NearbyPlace> restaurantsList) {
        mRestaurantsList = restaurantsList;
    }

    public Boolean getFineLocationPermission() {
        return mFineLocationPermission;
    }

    public void setFineLocationPermission(Boolean fineLocationPermission) {
        mFineLocationPermission = fineLocationPermission;
    }


    @NonNull
    @Override
    public String toString() {
        return  " \n#############################################\n" +
                "ModelUI\n"+
                "location Permission: "+ mFineLocationPermission+"\n"+
                "Location: " +mLocation+"\n"+
                "Number of Restaurants found : "+mRestaurantsList+"\n"+
                "#############################################";
    }
}
