package com.sbizzera.go4lunch.map_fragment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.VisibleRegion;
import com.sbizzera.go4lunch.LiveDataTestUtil;
import com.sbizzera.go4lunch.map_fragment.models.MapFragmentModel;
import com.sbizzera.go4lunch.repositories.CurrentGPSLocationRepo;
import com.sbizzera.go4lunch.repositories.PermissionRepo;
import com.sbizzera.go4lunch.repositories.VisibleRegionRepo;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreRestaurant;
import com.sbizzera.go4lunch.repositories.google_places.GooglePlacesRepo;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MapFragmentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MapFragmentViewModel viewModel;

    private MutableLiveData<Location> currentGPSLocationLD = new MutableLiveData<>();
    private MutableLiveData<List<NearbyPlace>> listNearbyRestaurantsLD = new MutableLiveData<>();
    private MutableLiveData<List<FireStoreRestaurant>> allKnownRestaurantsLD = new MutableLiveData<>();
    private MutableLiveData<VisibleRegion> lastMapVisibleRegionLD = new MutableLiveData<>();
    private MutableLiveData<VisibleRegion> lastNearbyRestaurantsFetchVisibleRegionLD = new MutableLiveData<>();
    private Boolean locationPermissionGranted = false;


    @Before
    public void setUp() throws Exception {
        CurrentGPSLocationRepo currentGPSLocationRepo = mock(CurrentGPSLocationRepo.class);
        doReturn(currentGPSLocationLD).when(currentGPSLocationRepo).getCurrentGPSLocationLD();

        GooglePlacesRepo googlePlacesRepo = mock(GooglePlacesRepo.class);
        doReturn(listNearbyRestaurantsLD).when(googlePlacesRepo).getNearbyRestaurants(anyString(), anyInt());

        FireStoreRepo fireStoreRepo = mock(FireStoreRepo.class);
        doReturn(allKnownRestaurantsLD).when(fireStoreRepo).getAllKnownRestaurants();

        VisibleRegionRepo visibleRegionRepo = mock(VisibleRegionRepo.class);
        doReturn(lastMapVisibleRegionLD).when(visibleRegionRepo).getLastMapVisibleRegion();
        doReturn(lastNearbyRestaurantsFetchVisibleRegionLD).when(visibleRegionRepo).getLastNearbyRestaurantsFetchVisibleRegion();

        PermissionRepo permissionRepo = mock(PermissionRepo.class);
        doReturn(locationPermissionGranted).when(permissionRepo).isLocationPermissionGranted();


        viewModel = new MapFragmentViewModel(
                currentGPSLocationRepo,
                googlePlacesRepo,
                fireStoreRepo,
                visibleRegionRepo,
                permissionRepo
                );

    }

    //TODO test values but also model if !MapReady, !HasMapBeenInitialised,...


    @Test
    public void getUIModel() throws InterruptedException{
        MapFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getUIModel());
        assertNull(model.getMapMarkersList());
        assertNull(model.getLastSeenLatLngBounds());
        assertFalse(model.isSearchButtonVisible());
        assertFalse(model.isCenterOnLocationButtonVisible());
        assertNull(model.getCurrentGPSLatLng());
    }
}