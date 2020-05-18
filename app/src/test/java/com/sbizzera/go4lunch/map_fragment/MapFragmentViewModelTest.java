package com.sbizzera.go4lunch.map_fragment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MapFragmentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MapFragmentViewModel viewModel;

    private MutableLiveData<android.location.Location> currentGPSLocationLD = new MutableLiveData<>();
    private MutableLiveData<List<NearbyPlace>> listNearbyRestaurantsLD = new MutableLiveData<>();
    private MutableLiveData<List<FireStoreRestaurant>> allKnownRestaurantsLD = new MutableLiveData<>();
    private MutableLiveData<VisibleRegion> lastMapVisibleRegionLD = new MutableLiveData<>();
    private MutableLiveData<VisibleRegion> lastNearbyRestaurantsFetchVisibleRegionLD = new MutableLiveData<>();


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
        // a dynamiser
        doReturn(false).when(permissionRepo).isLocationPermissionGranted();


        viewModel = new MapFragmentViewModel(
                currentGPSLocationRepo,
                googlePlacesRepo,
                fireStoreRepo,
                visibleRegionRepo,
                permissionRepo
        );

    }


    @Test
    public void shouldSendEmptyModelIfMapNotReady() throws InterruptedException {
        //Given
        mockLastMapVisibleRegionLD();
        mockFireStoreRestaurantsList();

        //When
        MapFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getUIModel());

        //Then
        assertNull(model.getMapMarkersList());
    }

    @Test
    public void shouldSendModelWhenMapIdReady() throws InterruptedException {
        //Given
        mockLastMapVisibleRegionLD();
        mockFireStoreRestaurantsList();
        viewModel.mapIsReady();

        //When
        MapFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getUIModel());

        //Then
        assertNotNull(model.getMapMarkersList());
        assertEquals(4, model.getMapMarkersList().size());
        assertThat(model.getMapMarkersList(), hasItem(
                hasProperty("restaurantId", is("restId1"))
        ));
    }

    @Test
    public void shouldSetCameraToGPSPositionIfLastMapVisibleRegionNull() throws InterruptedException {
        //Given
        viewModel.mapIsReady();
        mockCurrentGPSPosition();

        //When
        MapFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getUIModel());

        //Then
        assertNull(model.getLastSeenLatLngBounds());
        assertEquals(2.5, model.getCurrentGPSLatLng().latitude, 0.1);

    }

    @Test
    public void shouldSetCameraToLastMapVisibleRegionIfNotNull() throws InterruptedException {
        //Given
        viewModel.mapIsReady();
        mockCurrentGPSPosition();
        mockLastMapVisibleRegionLD();

        //When
        MapFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getUIModel());

        //Then
        assertEquals(5.0, model.getLastSeenLatLngBounds().northeast.latitude, 0.1);
        assertEquals(5.0, model.getLastSeenLatLngBounds().northeast.longitude, 0.1);
        assertNull(model.getCurrentGPSLatLng());
    }

    @Test
    public void shouldNotSetCameraIfCameraHasBeenInitialized() throws InterruptedException {
        //Given
        viewModel.mapIsReady();
        mockCurrentGPSPosition();
        mockLastMapVisibleRegionLD();
        //Camera has been moved
        viewModel.setLastVisibleRegion(null);

        //When
        MapFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getUIModel());

        //Then
        assertNull(model.getCurrentGPSLatLng());
        assertNull(model.getLastSeenLatLngBounds());
    }

    private void mockLastMapVisibleRegionLD() {
        lastMapVisibleRegionLD.setValue(
                new VisibleRegion(
                        new LatLng(0, 0),
                        new LatLng(0, 5),
                        new LatLng(5, 0),
                        new LatLng(5, 5),
                        new LatLngBounds(
                                new LatLng(0, 0),
                                new LatLng(5, 5))
                )
        );

    }

    private void mockFireStoreRestaurantsList() {
        List<FireStoreRestaurant> restaurantList = Arrays.asList(
                new FireStoreRestaurant("restId1", "restName1", 1d, 1d),
                new FireStoreRestaurant("restId2", "restName2", 2d, 2d),
                new FireStoreRestaurant("restId3", "restName3", 3d, 3d),
                new FireStoreRestaurant("restId4", "restName4", 4d, 4d)
        );
        allKnownRestaurantsLD.setValue(restaurantList);
    }

    private void mockCurrentGPSPosition() {
        android.location.Location location = mock(android.location.Location.class);
        doReturn(2.5).when(location).getLatitude();
        doReturn(2.5).when(location).getLongitude();
        currentGPSLocationLD.setValue(location);
    }

    private void mockLastNearbyFetchedVisibleRegion() {
        lastNearbyRestaurantsFetchVisibleRegionLD.setValue(
                new VisibleRegion(
                        new LatLng(1, 1),
                        new LatLng(1, 4),
                        new LatLng(4, 1),
                        new LatLng(4, 4),
                        new LatLngBounds(
                                new LatLng(1, 1),
                                new LatLng(4, 4)
                        )
                )
        );

    }


}