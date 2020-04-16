package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;

import com.google.android.gms.maps.model.CameraPosition;

import java.util.List;

public class MapFragmentModel {

    private List<CustomMapMarker> mapMarkersList ;
    private CameraPosition initialCameraPosition;
    private boolean isSearchButtonVisible;

    public MapFragmentModel(List<CustomMapMarker> mapMarkersList, CameraPosition initialCameraPosition, boolean isSearchButtonVisible) {
        this.mapMarkersList = mapMarkersList;
        this.initialCameraPosition = initialCameraPosition;
        this.isSearchButtonVisible = isSearchButtonVisible;
    }

    public List<CustomMapMarker> getMapMarkersList() {
        return mapMarkersList;
    }

    public CameraPosition getInitialCameraPosition() {
        return initialCameraPosition;
    }

    // TODO BORIS
    public boolean isSearchButtonVisible() {
        return isSearchButtonVisible;
    }
}
