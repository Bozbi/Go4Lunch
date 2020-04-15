package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;

import android.location.Location;

import com.google.android.gms.maps.model.CameraPosition;
import com.sbizzera.go4lunch.main_activity.fragments.map_fragment.CustomMapMarker;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentModel {

    private List<CustomMapMarker> mapMarkersList ;
    private CameraPosition initialCameraPosition;

    public MapFragmentModel(List<CustomMapMarker> mapMarkersList,CameraPosition initialCameraPosition) {
        this.mapMarkersList = mapMarkersList;
        this.initialCameraPosition = initialCameraPosition;
    }

    public List<CustomMapMarker> getMapMarkersList() {
        return mapMarkersList;
    }

    public CameraPosition getInitialCameraPosition() {
        return initialCameraPosition;
    }
}
