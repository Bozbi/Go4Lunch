package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;

import android.location.Location;

import com.sbizzera.go4lunch.main_activity.fragments.map_fragment.CustomMapMarker;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentModel {

    private List<CustomMapMarker> mapMarkersList ;

    public MapFragmentModel(List<CustomMapMarker> mapMarkersList) {
        this.mapMarkersList = mapMarkersList;
    }

    public List<CustomMapMarker> getMapMarkersList() {
        return mapMarkersList;
    }
}
