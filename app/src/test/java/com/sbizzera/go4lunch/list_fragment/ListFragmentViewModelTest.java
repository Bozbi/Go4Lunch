package com.sbizzera.go4lunch.list_fragment;

import android.content.Context;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.LiveDataTestUtil;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.list_fragment.models.ListFragmentModel;
import com.sbizzera.go4lunch.repositories.CurrentGPSLocationRepo;
import com.sbizzera.go4lunch.repositories.SortTypeChosenRepo;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreRestaurant;
import com.sbizzera.go4lunch.repositories.google_places.GooglePlacesRepo;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyGeometry;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyLocation;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.AddressComponent;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.DetailResult;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Geometry;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.OpeningHours;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Photos;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ListFragmentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public Context context;

    private ListFragmentViewModel viewModel;

    private MutableLiveData<Location> currentGPSLocationLD = new MutableLiveData<>();
    private MutableLiveData<Map<String, NearbyPlace>> nearbyCacheLD = new MutableLiveData<>();
    private MutableLiveData<List<FireStoreRestaurant>> allKnownRestaurantsLD = new MutableLiveData<>();
    private MutableLiveData<Integer> sortTypeChosenLD = new MutableLiveData<>();

    @Before
    public void setup() throws Exception {
        CurrentGPSLocationRepo currentGPSLocationRepo = mock(CurrentGPSLocationRepo.class);
        doReturn(currentGPSLocationLD).when(currentGPSLocationRepo).getCurrentGPSLocationLD();

        GooglePlacesRepo googlePlacesRepo = mock(GooglePlacesRepo.class);
        doReturn(nearbyCacheLD).when(googlePlacesRepo).getNearbyCacheLiveData();

        FireStoreRepo fireStoreRepo = mock(FireStoreRepo.class);
        doReturn(allKnownRestaurantsLD).when(fireStoreRepo).getAllKnownRestaurants();

        SortTypeChosenRepo sortTypeChosenRepo = mock(SortTypeChosenRepo.class);
        doReturn(sortTypeChosenLD).when(sortTypeChosenRepo).getSelectedChipID();


        viewModel = new ListFragmentViewModel(
                currentGPSLocationRepo,
                googlePlacesRepo,
                fireStoreRepo,
                sortTypeChosenRepo,
                context
        );

        FieldSetter.setField(viewModel,viewModel.getClass().getDeclaredField("mDetailsMapLD"),getDetailsMapLiveData());
    }

    @Test
    public void getModel() throws InterruptedException {
        Location location = getCurrentGPSLocation();
        currentGPSLocationLD.setValue(location);
        Map<String, NearbyPlace> map = getNearbyPlacesMap();
        nearbyCacheLD.setValue(map);
        List<FireStoreRestaurant> firestoreRestaurantsList = getFirestoreRestaurantsList();
        allKnownRestaurantsLD.setValue(firestoreRestaurantsList);

        sortTypeChosenLD.setValue(R.id.distance_chip);

        ListFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModel());

        assertEquals(3, model.getListAdapterModel().size());
    }

    private List<FireStoreRestaurant> getFirestoreRestaurantsList() {
        List<FireStoreRestaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new FireStoreRestaurant("restId5", "restName5", 5d, 5d));
        restaurantList.add(new FireStoreRestaurant("restId6", "restName6", 6d, 6d));
        restaurantList.add(new FireStoreRestaurant("restId7", "restName7", 7d, 7d));
        restaurantList.add(new FireStoreRestaurant("restId8", "restName8", 8d, 8d));
        return restaurantList;
    }

    private Map<String, NearbyPlace> getNearbyPlacesMap() {
        Map<String, NearbyPlace> map = new HashMap<>();
        map.put("restId1", new NearbyPlace("restId1", "restName1", new NearbyGeometry(new NearbyLocation(1d, 1d))));
        map.put("restId2", new NearbyPlace("restId2", "restName2", new NearbyGeometry(new NearbyLocation(2d, 2d))));
        map.put("restId3", new NearbyPlace("restId3", "restName3", new NearbyGeometry(new NearbyLocation(3d, 3d))));
        map.put("restId4", new NearbyPlace("restId4", "restName4", new NearbyGeometry(new NearbyLocation(4d, 4d))));
        return map;
    }

    private Location getCurrentGPSLocation() {
        Location location = new Location("");
        location.setLatitude(0);
        location.setLongitude(0);
        return location;
    }

    private LiveData<Map<String, DetailResult>> getDetailsMapLiveData() {
        MutableLiveData<Map<String, DetailResult>> deatailsMapLD = new MutableLiveData<>();
        Map<String, DetailResult> map = new HashMap<>();
        map.put("restId1",
                new DetailResult(
                        "restName1",
                        Arrays.asList(
                                new AddressComponent("1"),
                                new AddressComponent("rue du 1")),
                        "0101010101",
                        Arrays.asList(
                                new Photos("photoRef1a"), new Photos("photoRef1b")),
                        "www.site1.com",
                        "restId1",
                        new OpeningHours(true),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(1d, 1d)
                        )
                ));
        deatailsMapLD.setValue(map);

        //TODO return good Value
        return null;
    }
}