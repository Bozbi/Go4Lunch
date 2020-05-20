package com.sbizzera.go4lunch.list_fragment;

import android.content.Context;
import android.location.Location;
import android.view.View;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.BuildConfig;
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
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        MockitoAnnotations.initMocks(this);

        CurrentGPSLocationRepo currentGPSLocationRepo = mock(CurrentGPSLocationRepo.class);
        doReturn(currentGPSLocationLD).when(currentGPSLocationRepo).getCurrentGPSLocationLD();

        GooglePlacesRepo googlePlacesRepo = mock(GooglePlacesRepo.class);
        doReturn(nearbyCacheLD).when(googlePlacesRepo).getNearbyCacheLiveData();

        FireStoreRepo fireStoreRepo = mock(FireStoreRepo.class);
        doReturn(allKnownRestaurantsLD).when(fireStoreRepo).getAllKnownRestaurants();

        SortTypeChosenRepo sortTypeChosenRepo = mock(SortTypeChosenRepo.class);
        doReturn(sortTypeChosenLD).when(sortTypeChosenRepo).getSelectedChipID();

        doReturn("NoSchedule").when(context).getString(R.string.no_schedule_available);
        doReturn("Open").when(context).getString(R.string.open_now);
        doReturn("Closed").when(context).getString(R.string.closed);


        viewModel = new ListFragmentViewModel(
                currentGPSLocationRepo,
                googlePlacesRepo,
                fireStoreRepo,
                sortTypeChosenRepo,
                context
        );

        FieldSetter.setField(viewModel, viewModel.getClass().getDeclaredField("mDetailsMapLD"), getDetailsMapLiveData());

    }

    @Test
    public void allRestaurantDataShouldBeMappedCorrectlyToModel() throws InterruptedException {
        mockCurrentGPSPosition();
        Map<String, NearbyPlace> map = getNearbyPlacesMap();
        nearbyCacheLD.setValue(map);
        List<FireStoreRestaurant> firestoreRestaurantsList = getFirestoreRestaurantsList();
        allKnownRestaurantsLD.setValue(firestoreRestaurantsList);

        sortTypeChosenLD.setValue(R.id.frequentation_chip);

        ListFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModel());

        assert model.getSortId() != null;
        assertEquals(R.id.frequentation_chip, model.getSortId(), 0);
        assertEquals(8, model.getListAdapterModel().size());
        assertEquals("restName7", model.getListAdapterModel().get(0).getRestaurantName());
        assertEquals("restName6", model.getListAdapterModel().get(1).getRestaurantName());
        assertEquals("restName8", model.getListAdapterModel().get(2).getRestaurantName());
        assertEquals("restName5", model.getListAdapterModel().get(3).getRestaurantName());

        sortTypeChosenLD.setValue(R.id.likes_chip);

        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModel());

        assert model.getSortId() != null;
        assertEquals(R.id.likes_chip, model.getSortId(), 0);
        assertEquals("restName6", model.getListAdapterModel().get(0).getRestaurantName());
        assertEquals("restName5", model.getListAdapterModel().get(1).getRestaurantName());
        assertEquals("restName8", model.getListAdapterModel().get(2).getRestaurantName());
        assertEquals("restName7", model.getListAdapterModel().get(3).getRestaurantName());


        assertEquals(R.color.quantum_grey500, model.getListAdapterModel().get(0).getOpenHoursTextColor());
        assertEquals("NoSchedule", model.getListAdapterModel().get(0).getOpenHoursText());
        assertEquals(R.color.closed, model.getListAdapterModel().get(1).getOpenHoursTextColor());
        assertEquals("Closed", model.getListAdapterModel().get(1).getOpenHoursText());
        assertEquals(R.color.open, model.getListAdapterModel().get(4).getOpenHoursTextColor());
        assertEquals("Open", model.getListAdapterModel().get(4).getOpenHoursText());

        assertEquals(View.VISIBLE, model.getListAdapterModel().get(0).getStar1Visibility());
        assertEquals(View.VISIBLE, model.getListAdapterModel().get(0).getStar2Visibility());
        assertEquals(View.VISIBLE, model.getListAdapterModel().get(0).getStar3Visibility());

        assertEquals(View.VISIBLE, model.getListAdapterModel().get(1).getStar1Visibility());
        assertEquals(View.VISIBLE, model.getListAdapterModel().get(1).getStar2Visibility());
        assertEquals(View.VISIBLE, model.getListAdapterModel().get(1).getStar3Visibility());

        assertEquals(View.VISIBLE, model.getListAdapterModel().get(2).getStar1Visibility());
        assertEquals(View.VISIBLE, model.getListAdapterModel().get(2).getStar2Visibility());
        assertEquals(View.INVISIBLE, model.getListAdapterModel().get(2).getStar3Visibility());

        assertEquals(View.INVISIBLE, model.getListAdapterModel().get(3).getStar1Visibility());
        assertEquals(View.INVISIBLE, model.getListAdapterModel().get(3).getStar2Visibility());
        assertEquals(View.INVISIBLE, model.getListAdapterModel().get(3).getStar3Visibility());

        assertEquals("6, rue du 6", model.getListAdapterModel().get(0).getRestaurantAddress());

        assertEquals("restId6", model.getListAdapterModel().get(0).getRestaurantId());

        assertEquals("https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=photoRef6a&key=" + BuildConfig.GOOGLE_API_KEY, model.getListAdapterModel().get(0).getPhotoUrl());

        assertEquals(2.5, model.getListAdapterModel().get(0).getDistance(), 1);
        assertEquals(View.VISIBLE, model.getListAdapterModel().get(0).getMetersTextVisibility());
    }

    private List<FireStoreRestaurant> getFirestoreRestaurantsList() {
        List<FireStoreRestaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new FireStoreRestaurant("restId5", "restName5", 5d, 5d, Arrays.asList("user1", "user2", "user3"), 1));
        restaurantList.add(new FireStoreRestaurant("restId6", "restName6", 6d, 6d, Arrays.asList("user1", "user2", "user3", "user4"), 9));
        restaurantList.add(new FireStoreRestaurant("restId7", "restName7", 7d, 7d, new ArrayList<>(), 10));
        restaurantList.add(new FireStoreRestaurant("restId8", "restName8", 8d, 8d, Arrays.asList("user1", "user2"), 5));
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


    private void mockCurrentGPSPosition() {
        android.location.Location location = mock(android.location.Location.class);
        doReturn(0D).when(location).getLatitude();
        doReturn(0D).when(location).getLongitude();
        doReturn(2500F).when(location).distanceTo(any(Location.class));
        currentGPSLocationLD.setValue(location);
    }

    private LiveData<Map<String, DetailResult>> getDetailsMapLiveData() {
        MutableLiveData<Map<String, DetailResult>> detailsMapLD = new MutableLiveData<>();
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
        map.put("restId2",
                new DetailResult(
                        "restName2",
                        Arrays.asList(
                                new AddressComponent("2"),
                                new AddressComponent("rue du 2")),
                        "0202020202",
                        Arrays.asList(
                                new Photos("photoRef2a"), new Photos("photoRef2b")),
                        "www.site2.com",
                        "restId2",
                        new OpeningHours(true),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(2d, 2d)
                        )
                ));
        map.put("restId3",
                new DetailResult(
                        "restName3",
                        Arrays.asList(
                                new AddressComponent("3"),
                                new AddressComponent("rue du 3")),
                        "0303030303",
                        Arrays.asList(
                                new Photos("photoRef3a"), new Photos("photoRef3b")),
                        "www.site3.com",
                        "restId3",
                        new OpeningHours(true),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(3d, 3d)
                        )
                ));
        map.put("restId4",
                new DetailResult(
                        "restName4",
                        Arrays.asList(
                                new AddressComponent("4"),
                                new AddressComponent("rue du 4")),
                        "0404040404",
                        Arrays.asList(
                                new Photos("photoRef4a"), new Photos("photoRef4b")),
                        "www.site4.com",
                        "restId4",
                        new OpeningHours(true),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(4d, 4d)
                        )
                ));
        map.put("restId5",
                new DetailResult(
                        "restName5",
                        Arrays.asList(
                                new AddressComponent("5"),
                                new AddressComponent("rue du 5")),
                        "0505050505",
                        Arrays.asList(
                                new Photos("photoRef5a"), new Photos("photoRef5b")),
                        "www.site5.com",
                        "restId5",
                        new OpeningHours(false),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(5d, 5d)
                        )
                ));
        map.put("restId6",
                new DetailResult(
                        "restName6",
                        Arrays.asList(
                                new AddressComponent("6"),
                                new AddressComponent("rue du 6")),
                        "0606060606",
                        Arrays.asList(
                                new Photos("photoRef6a"), new Photos("photoRef6b")),
                        "www.site6.com",
                        "restId6",
                        new OpeningHours(null),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(6d, 6d)
                        )
                ));
        map.put("restId7",
                new DetailResult(
                        "restName7",
                        Arrays.asList(
                                new AddressComponent("7"),
                                new AddressComponent("rue du 7")),
                        "0707070707",
                        Arrays.asList(
                                new Photos("photoRef7a"), new Photos("photoRef7b")),
                        "www.site7.com",
                        "restId7",
                        new OpeningHours(false),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(7d, 7d)
                        )
                ));
        map.put("restId8",
                new DetailResult(
                        "restName8",
                        Arrays.asList(
                                new AddressComponent("8"),
                                new AddressComponent("rue du 8")),
                        "0808080808",
                        Arrays.asList(
                                new Photos("photoRef8a"), new Photos("photoRef8b")),
                        "www.site8.com",
                        "restId8",
                        new OpeningHours(false),
                        new Geometry(
                                new com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location(8d, 8d)
                        )
                ));
        detailsMapLD.setValue(map);

        return detailsMapLD;
    }
}