package com.sbizzera.go4lunch.main_activity.fragments.map_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.main_activity.RestaurantClickedListenable;
import com.sbizzera.go4lunch.services.ViewModelFactory;

public class MapFragment
    extends SupportMapFragment
    implements
    OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMapLoadedCallback,
    RestaurantClickedListenable {

    private GoogleMap map;
    private OnItemBoundWithRestaurantClickListener mListener;
    private MapFragmentViewModel mViewModel;
    private Button mFetchNewAreaBtn;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        //Inserting Button over SupportMapFragment
        ConstraintLayout layout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_map_fragment_overlay, viewGroup, false);
        View v = super.onCreateView(layoutInflater, viewGroup, bundle);
        layout.addView(v, 0);
        mFetchNewAreaBtn = layout.findViewById(R.id.new_restaurants_btn);
        getActivity().setTitle(getString(R.string.map_title_bar_title));
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapFragmentViewModel.class);
        mViewModel.getUIModel().observe(this, this::updateUi);
        getMapAsync(this);
    }

    private void updateUi(MapFragmentModel model) {
        if (model.getMapMarkersList() != null) {
            for (CustomMapMarker marker : model.getMapMarkersList()) {
                Marker newMarker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(marker.getLat(), marker.getLng()))
                        .title(marker.getRestaurantName())
                        .icon(BitMapCreator.bitmapDescriptorFromVector(requireActivity(), marker.getMarkerIcon()))
                );
                newMarker.setTag(marker.getRestaurantId());

            }
            // TODO BOZBI Attention tu mets à jour ton listener à chaque mise à jour de LiveData, ne le fait qu'une fois pendant onMapReady
            map.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(marker.getPosition(),18);
                map.animateCamera(location);
                return true;
            });
            // TODO BOZBI La même
            map.setOnInfoWindowClickListener(marker -> {
                if (marker.getTag() != null) {
                    mListener.onItemBoundWithRestaurantClick(marker.getTag().toString());
                    marker.hideInfoWindow();
                }
            });
        }
        if (model.getCurrentGPSLatLng() != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(model.getCurrentGPSLatLng(), 15));
        }
        if (model.getLastSeenLatLngBounds() != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(model.getLastSeenLatLngBounds().getCenter(), 15));
        }

        if (model.isCenterOnLocationButtonVisible()) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }
        // TODO BOZBI Le visibility ne suffit pas ? Pas besoin de mettre clickable à false en plus
        mFetchNewAreaBtn.setClickable(false);
        mFetchNewAreaBtn.setVisibility(View.INVISIBLE);
        if (model.isSearchButtonVisible()) {
            mFetchNewAreaBtn.setVisibility(View.VISIBLE);
            mFetchNewAreaBtn.setClickable(model.isSearchButtonVisible());
        }
        // TODO BOZBI Attention tu mets à jour ton listener à chaque mise à jour de LiveData, ne le fait qu'une fois pendant onMapReady
        mFetchNewAreaBtn.setOnClickListener((click) -> {
            mViewModel.setLastFetchRestaurantVisibleRegion(map.getProjection().getVisibleRegion());
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        mViewModel.mapIsReady();
    }

    @Override
    public void onMapLoaded() {
        mViewModel.setLastVisibleRegion(map.getProjection().getVisibleRegion());
        map.setOnCameraIdleListener(this);
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onCameraIdle() {
        mViewModel.setLastVisibleRegion(map.getProjection().getVisibleRegion());
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.mapIsDestroyed();
    }
}
