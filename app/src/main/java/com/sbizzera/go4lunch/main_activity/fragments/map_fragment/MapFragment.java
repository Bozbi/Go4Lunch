package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.services.ViewModelFactory;
import com.sbizzera.go4lunch.utils.Commons;

import timber.log.Timber;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLoadedCallback {

    private static final int REQUEST_LOCATION_PERMISSION_REQUEST_CODE = 123;

    private GoogleMap map;
    private OnItemBoundWithRestaurantClickListener mListener;
    private MapFragmentViewModel mViewModel;
    private Button fetchNewAreaBtn;

    public static MapFragment newInstance() {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        //Inserting Button over SupportMapFragment
        ConstraintLayout layout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_map_fragment_overlay, viewGroup, false);
        View v = super.onCreateView(layoutInflater, viewGroup, bundle);
        layout.addView(v, 0);
        fetchNewAreaBtn = layout.findViewById(R.id.new_restaurants_btn);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapFragmentViewModel.class);
        mViewModel.getUIModel().observe(this, this::updateUi);
        mViewModel.getAction().observe(this, action -> {
            if (action == MapFragmentViewModel.ViewAction.FETCH_NEW_AREA_VISIBLE) {
                fetchNewAreaBtn.setClickable(true);
                fetchNewAreaBtn.setVisibility(View.VISIBLE);
                fetchNewAreaBtn.setOnClickListener(v -> {
                    mViewModel.changeLocationSourceLD(map.getCameraPosition().target, map.getProjection().getVisibleRegion().latLngBounds);
                });
            }
            if (action == MapFragmentViewModel.ViewAction.FETCH_NEW_AREA_INVISIBLE) {
                fetchNewAreaBtn.setClickable(false);
                fetchNewAreaBtn.setVisibility(View.INVISIBLE);
            }
            if (action == MapFragmentViewModel.ViewAction.ASK_LOCATION_PERMISSION){
                showPermissionAppropriateRequest();
            }
        });

    }

    private void showPermissionAppropriateRequest() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(),R.style.AppTheme));

            builder.setTitle("Permission is Mandatory");
            builder.setMessage("We need permission to give you the best experience navigating the map");
            builder.setNegativeButton("BACK", (x, y) -> {
            });
            builder.setPositiveButton("Go to permissions", (x, y) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            builder.show();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateUi(MapFragmentModel model) {
        if (model.getMapMarkersList() != null) {
            for (CustomMapMarker marker : model.getMapMarkersList()) {
                Marker newMarker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(marker.getLat(), marker.getLng()))
                        .title(marker.getRestaurantName())
                        .snippet(marker.getRestaurantName())
                        .icon(BitMapCreator.bitmapDescriptorFromVector(requireActivity(), marker.getMarkerIcon()))
                );
                newMarker.setTag(marker.getRestaurantId());
            }
            map.setOnMarkerClickListener(marker -> {
                mListener.onItemBoundWithRestaurantClick(marker.getTag().toString());
                return true;
            });
        }
        if(model.getInitialCameraPosition()!=null){
            map.moveCamera(CameraUpdateFactory.newCameraPosition(model.getInitialCameraPosition()));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mViewModel.mapIsReady(true);
        map.setOnMapLoadedCallback(this);
        Timber.d("Map is ready");
    }

    @Override
    public void onMapLoaded() {
        map.setOnCameraIdleListener(this);
        mViewModel.setLastVisibleRegion(map.getProjection().getVisibleRegion());
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onCameraIdle() {
        Timber.d("camera moved");
        mViewModel.setLastCameraPosition(map.getCameraPosition());
        LatLng cameraLatLng = map.getCameraPosition().target;
        LatLngBounds cameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
        mViewModel.shouldNewAreaFetchBeVisible(cameraLatLng, cameraBounds);
        mViewModel.setLastVisibleRegion(map.getProjection().getVisibleRegion());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.mapIsReady(false);
    }
}
