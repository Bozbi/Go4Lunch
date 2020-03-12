package com.sbizzera.go4lunch.views.fragments;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.MapFragmentModel;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.utils.BitMapCreator;
import com.sbizzera.go4lunch.utils.Commons;
import com.sbizzera.go4lunch.view_models.MapFragmentViewModel;
import com.sbizzera.go4lunch.view_models.MapFragmentViewModelFactory;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 123;

    private GoogleMap map;

    private MapFragmentViewModel mViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Binding View with Gmap
        getMapAsync(this);

        mViewModel = new ViewModelProvider(this, MapFragmentViewModelFactory.getInstance(requireActivity())).get(MapFragmentViewModel.class);

        mViewModel.getUIModel().observe(this, model -> {
            if (model.getFineLocationPermission()) {
                updateUi(model);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            }
        });

    }

    private void updateUi(MapFragmentModel model) {
        if (model.getLocation() != null) {
            Log.d(TAG, "updateUi: moving camera");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLocation().getLatitude(), model.getLocation().getLongitude()), Commons.DEFAULT_ZOOM));
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);

        }
        if (model.getRestaurantsList() != null) {
            for (NearbyPlace restaurant : model.getRestaurantsList()) {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(restaurant.getLat(), restaurant.getLng()))
                        .title(restaurant.getName())
                        .icon(BitMapCreator.bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_restaurant_marker_icon)));
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //TODO need to work on GPS Access and Permissions
        mViewModel.updatePermissionAndLocation();
    }
}
