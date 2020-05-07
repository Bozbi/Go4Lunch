package com.sbizzera.go4lunch.map_fragment.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class MapFragmentModel {

    private List<CustomMapMarker> mapMarkersList;
    private LatLngBounds lastSeenLatLngBounds;
    private LatLng currentGPSLatLng;
    private boolean isSearchButtonVisible;
    private boolean isCenterOnLocationButtonVisible;

    public MapFragmentModel(List<CustomMapMarker> mapMarkersList, LatLngBounds lastSeenLatLngBounds, boolean isSearchButtonVisible, boolean isCenterOnLocationButtonVisible, LatLng currentGPSLatLng) {
        this.mapMarkersList = mapMarkersList;
        this.lastSeenLatLngBounds = lastSeenLatLngBounds;
        this.isSearchButtonVisible = isSearchButtonVisible;
        this.isCenterOnLocationButtonVisible = isCenterOnLocationButtonVisible;
        this.currentGPSLatLng = currentGPSLatLng;
    }

    public List<CustomMapMarker> getMapMarkersList() {
        return mapMarkersList;
    }

    public boolean isSearchButtonVisible() {
        return isSearchButtonVisible;
    }

    public boolean isCenterOnLocationButtonVisible() {
        return isCenterOnLocationButtonVisible;
    }

    public LatLngBounds getLastSeenLatLngBounds() {
        return lastSeenLatLngBounds;
    }

    public LatLng getCurrentGPSLatLng() {
        return currentGPSLatLng;
    }
}
