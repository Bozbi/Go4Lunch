package com.sbizzera.go4lunch.restaurant_activity;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreUser;
import com.sbizzera.go4lunch.repositories.google_places.GooglePlacesRepo;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.DetailResult;
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
        BDDMockito.given(placeDetailLiveData.getValue()).willReturn(new DetailResult());
        BDDMockito.given(placeDetailLiveData.getValue().getAddressComponentList().get(0).getValue()).willReturn("6");
        BDDMockito.given(placeDetailLiveData.getValue().getAddressComponentList().get(1).getValue()).willReturn("rue du test");
        BDDMockito.given(placeDetailLiveData.getValue().getName()).willReturn("Au bon test");
        BDDMockito.given(placeDetailLiveData.getValue().getPhotosList().get(0).getPhotoReference()).willReturn("www.photoTestUrl.com");
        BDDMockito.given(placeDetailLiveData.getValue().getPhoneNumber()).willReturn("0606060606");





        isRestaurantLikedByUserLiveData.setValue(true);
        restaurantLikeCountLiveData.setValue(10);
        isRestaurantTodayUserChoiceLiveData.setValue(false);
        todayListOfUsersLiveData.setValue(Arrays.asList(new
                FireStoreUser("testUser", "Bobo", "www.testUserPhoto.com")));
//        Assert.assertEquals(viewModel.getModelLiveData().getValue(),
//                new RestaurantActivityModel(
//                        "www.testUserPhoto.com",
//                        R.drawable.restaurant_icon_grey,
//                        R.color.white,
//
//
//                        ));
        viewModel.fetchRestaurantInfo("1234");
    }
}