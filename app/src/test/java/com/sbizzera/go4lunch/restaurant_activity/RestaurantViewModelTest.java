package com.sbizzera.go4lunch.restaurant_activity;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreUser;
import com.sbizzera.go4lunch.repositories.google_places.GooglePlacesRepo;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.AddressComponent;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.DetailResult;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Geometry;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Location;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.OpeningHours;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.Photos;
import com.sbizzera.go4lunch.restaurant_activity.models.RestaurantActivityModel;
import com.sbizzera.go4lunch.services.AuthHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    Context context;

    private MutableLiveData<DetailResult> placeDetailLiveData;
    private MutableLiveData<Boolean> isRestaurantLikedByUserLiveData;
    private MutableLiveData<Integer> restaurantLikeCountLiveData;
    private MutableLiveData<Boolean> isRestaurantTodayUserChoiceLiveData;
    private MutableLiveData<List<FireStoreUser>> todayListOfUsersLiveData;


    private RestaurantViewModel viewModel;


    @Before
    public void setUp() throws Exception {
        GooglePlacesRepo googlePlacesRepo = mock(GooglePlacesRepo.class);
        FireStoreRepo fireStoreRepo = mock(FireStoreRepo.class);
        AuthHelper authHelper = mock(AuthHelper.class);


        placeDetailLiveData = new MutableLiveData<>();
        isRestaurantLikedByUserLiveData = new MutableLiveData<>();
        restaurantLikeCountLiveData = new MutableLiveData<>();
        isRestaurantTodayUserChoiceLiveData = new MutableLiveData<>();
        todayListOfUsersLiveData = new MutableLiveData<>();
        FirebaseUser mockFirebaseUser = mock(FirebaseUser.class);


        Mockito.doReturn(placeDetailLiveData).when(googlePlacesRepo).getRestaurantDetailsById(null);
        Mockito.doReturn(isRestaurantLikedByUserLiveData).when(fireStoreRepo).isRestaurantLikedByUser(null, null);
        Mockito.doReturn(restaurantLikeCountLiveData).when(fireStoreRepo).getRestaurantLikesCount(null);
        Mockito.doReturn(isRestaurantTodayUserChoiceLiveData).when(fireStoreRepo).isRestaurantChosenByUserToday(null, null);
        Mockito.doReturn(todayListOfUsersLiveData).when(fireStoreRepo).getTodayListOfUsers("1234");
        Mockito.doReturn(mockFirebaseUser).when(authHelper).getUser();


        viewModel = new RestaurantViewModel(googlePlacesRepo, fireStoreRepo, authHelper, context);
    }

    @Test
    public void shouldMapCorrectlyListOfFireStoreUsersToRestaurantActivityModel() {
        //Given
        DetailResult detailResult = new DetailResult(
                "testName",
                Arrays.asList(
                        new AddressComponent("6"),
                        new AddressComponent("rue du Test")),
                "0606060606",
                Arrays.asList(
                        new Photos("www.photo1.com"),
                        new Photos("www.photo2.com")),
                "www.test.com",
                "1234",
                new OpeningHours(true),
                new Geometry(new Location(10.00,10.00))
        );
        placeDetailLiveData.setValue(detailResult);
        isRestaurantLikedByUserLiveData.setValue(true);
        restaurantLikeCountLiveData.setValue(10);
        isRestaurantTodayUserChoiceLiveData.setValue(true);
        List<FireStoreUser> users = Arrays.asList(
                new FireStoreUser("testeur1ID","testeur1","www.photoTesteurAvatar.com"),
                new FireStoreUser("testeur2ID","testeur2","www.photoTesteurAvatar.com")
        );
        todayListOfUsersLiveData.setValue(users);

        //When
        viewModel.fetchRestaurantInfo("1234");
        RestaurantActivityModel model = viewModel.getModelLiveData().getValue();

        Assert.assertEquals("6 rue du Test",model.getAddressText());

    }
}