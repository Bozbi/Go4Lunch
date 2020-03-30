package com.sbizzera.go4lunch.views.fragments;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.model.CustomMapMarker;
import com.sbizzera.go4lunch.model.MapFragmentModel;
import com.sbizzera.go4lunch.utils.BitMapCreator;
import com.sbizzera.go4lunch.utils.Commons;
import com.sbizzera.go4lunch.view_models.MapFragmentViewModel;
import com.sbizzera.go4lunch.view_models.ViewModelFactory;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {


    private static final String TAG = "MapFragment";
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 123;

    private GoogleMap map;

    private MapFragmentViewModel mViewModel;

    private OnItemBoundWithRestaurantClickListener mListener;


    public MapFragment(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        //Inserting Button over SupportMapFragment
        ConstraintLayout layout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_map_fragment_overlay, viewGroup, false);
        View v = super.onCreateView(layoutInflater, viewGroup, bundle);
        layout.addView(v, 0);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapFragmentViewModel.class);
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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLocation().getLatitude(), model.getLocation().getLongitude()), Commons.DEFAULT_ZOOM));
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        if (model.getMapMarkersList() != null) {
            for (CustomMapMarker marker : model.getMapMarkersList()) {
                Marker newMarker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(marker.getLat(), marker.getLng()))
                        .title(marker.getRestaurantName())
                        .icon(BitMapCreator.bitmapDescriptorFromVector(requireActivity(), marker.getMarkerIcon()))
                );
                newMarker.setTag(marker.getRestaurantId());
            }
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mListener.onItemBoundWithRestaurantClick(marker.getTag().toString());
                    return true;
                }
            });
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
