package com.sbizzera.go4lunch.views.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sbizzera.go4lunch.PlacesAPI;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyResults;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";


    private static final int PERMISSION_ACCESS_FINE_LOCATION_CODE = 1;
    private static final float DEFAULT_ZOOM = 14;
    private MapView mMapView;
    private View mView;
    private GoogleMap map;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    private List<NearbyPlace> nearbyRestaurant;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mMapView = view.findViewById(R.id.mapView);

        //Loading GMap
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style_json));
                    map.setMyLocationEnabled(true);
                }
            });
        }


        //Check if location_fine permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //case permission is granted, get DeviceLocation
            Timber.d("location permission granted");
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            Timber.d("location found");
                            //Move Camera
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            //FetchRestaurant
                            Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://maps.googleapis.com/").build();
                            PlacesAPI placesAPI = retrofit.create(PlacesAPI.class);
                            nearbyRestaurant = new ArrayList<>();


                            String location = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
                            int radius = 1000;
                            String type = "restaurant";
                            String key = getResources().getString(R.string.google_places_API_key);

                            placesAPI.getNearbyRestaurant(location, radius, type, key).enqueue(new Callback<NearbyResults>() {
                                @Override
                                public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                                    Timber.d("Restaurants found");
                                    //Add Markers
                                    List<NearbyPlace> restaurantList = response.body().getRestaurantList();
                                    Timber.d(String.valueOf(restaurantList.size()));
                                    for (NearbyPlace restaurant : restaurantList
                                    ) {
                                        Log.d(TAG, String.valueOf(restaurant.getLat()));
                                        nearbyRestaurant.add(restaurant);
                                        map.addMarker(new MarkerOptions()
                                                .position(new LatLng(restaurant.getLat(), restaurant.getLng()))
                                                .title(restaurant.getName())
                                                .icon(bitmapDescriptorFromVector(requireActivity(),R.drawable.ic_restaurant_marker_icon)));

                                    }
                                }

                                @Override
                                public void onFailure(Call<NearbyResults> call, Throwable t) {
                                    Log.d(TAG, t.getMessage());
                                }
                            });

                        }
                    }
                }
            });
        } else {
            //case permission denied ask for permission
            //TODO onRequestResult actions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION_CODE);
        }

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() , vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




}
