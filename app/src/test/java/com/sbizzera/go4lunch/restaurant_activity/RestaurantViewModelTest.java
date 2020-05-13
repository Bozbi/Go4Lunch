package com.sbizzera.go4lunch.restaurant_activity;

import android.content.Context;
import android.view.View;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.BuildConfig;
import com.sbizzera.go4lunch.LiveDataTestUtil;
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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
    private RestaurantActivityModel model;
    private DetailResult detailResult;


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


        doReturn(placeDetailLiveData).when(googlePlacesRepo).getRestaurantDetailsById("1234");
        doReturn(isRestaurantLikedByUserLiveData).when(fireStoreRepo).isRestaurantLikedByUser("1234", null);
        doReturn(restaurantLikeCountLiveData).when(fireStoreRepo).getRestaurantLikesCount("1234");
        doReturn(isRestaurantTodayUserChoiceLiveData).when(fireStoreRepo).isRestaurantChosenByUserToday("1234", null);
        doReturn(todayListOfUsersLiveData).when(fireStoreRepo).getTodayListOfUsers("1234");
        doReturn(mockFirebaseUser).when(authHelper).getUser();


        viewModel = new RestaurantViewModel(googlePlacesRepo, fireStoreRepo, authHelper, context);
    }

    @Test
    public void shouldMapCorrectlyAllDataToRestaurantActivityModel() throws InterruptedException {
        //Given
        givenRoutine();

        //When
        viewModel.fetchRestaurantInfo("1234");
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference=photoref1&key=" + BuildConfig.GOOGLE_API_KEY, model.getPhotoUrl());
        assertEquals(R.drawable.restaurant_icon_white, model.getFabIcon());
        assertEquals(R.color.green, model.getFabColor());
        assertEquals("testName", model.getRestaurantName());
        assertEquals("6 rue du Test", model.getAddressText());
        assertEquals(View.VISIBLE, model.getStar1Visibility());
        assertEquals(View.INVISIBLE, model.getStar2Visibility());
        assertEquals(View.INVISIBLE, model.getStar3Visibility());
        assertEquals("0606060606", model.getPhoneNumber());
        assertEquals(R.color.colorPrimary, model.getPhoneColor());
        assertEquals(true, model.getPhoneClickable());
        assertEquals(R.drawable.ic_star_yellow, model.getLikeIcon());
        assertEquals("www.test.com", model.getWebSiteUrl());
        assertEquals(R.color.colorPrimary, model.getWebSiteColor());
        assertEquals(true, model.getWebSiteClickable());
        assertEquals(2, model.getWorkmatesList().size());
        assertThat(model.getWorkmatesList(), hasItem(
                hasProperty("photoUrl", Matchers.is("www.photoTesteur1Avatar.com"))
        ));
        assertThat(model.getWorkmatesList(), hasItem(
                hasProperty("text", Matchers.is("testeur1 eats here"))
        ));
        assertThat(model.getWorkmatesList(), hasItem(
                hasProperty("photoUrl", Matchers.is("www.photoTesteur2Avatar.com"))
        ));
        assertThat(model.getWorkmatesList(), hasItem(
                hasProperty("text", Matchers.is("testeur2 eats here"))
        ));

    }


    @Test
    public void shouldGiveCorrectStarNumbers() throws InterruptedException {
        //Given
        givenRoutine();
        restaurantLikeCountLiveData.setValue(0);

        //When
        viewModel.fetchRestaurantInfo("1234");
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals(View.INVISIBLE, model.getStar1Visibility());
        assertEquals(View.INVISIBLE, model.getStar2Visibility());
        assertEquals(View.INVISIBLE, model.getStar3Visibility());

        //Given
        restaurantLikeCountLiveData.setValue(1);

        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());


        //Then
        assertEquals(View.VISIBLE, model.getStar1Visibility());
        assertEquals(View.INVISIBLE, model.getStar2Visibility());
        assertEquals(View.INVISIBLE, model.getStar3Visibility());

        //Given
        restaurantLikeCountLiveData.setValue(2);

        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());


        //Then
        assertEquals(View.VISIBLE, model.getStar1Visibility());
        assertEquals(View.VISIBLE, model.getStar2Visibility());
        assertEquals(View.INVISIBLE, model.getStar3Visibility());

        //Given
        restaurantLikeCountLiveData.setValue(3);

        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());


        //Then
        assertEquals(View.VISIBLE, model.getStar1Visibility());
        assertEquals(View.VISIBLE, model.getStar2Visibility());
        assertEquals(View.VISIBLE, model.getStar3Visibility());

        //Given
        restaurantLikeCountLiveData.setValue(1000);

        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());


        //Then
        assertEquals(View.VISIBLE, model.getStar1Visibility());
        assertEquals(View.VISIBLE, model.getStar2Visibility());
        assertEquals(View.VISIBLE, model.getStar3Visibility());

    }

    @Test
    public void shouldGiveCorrectFabIconAndColors() throws InterruptedException {
        //Given
        givenRoutine();
        isRestaurantTodayUserChoiceLiveData.setValue(false);

        //When
        viewModel.fetchRestaurantInfo("1234");
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals(R.drawable.restaurant_icon_grey, model.getFabIcon());
        assertEquals(R.color.white, model.getFabColor());

        //Given
        isRestaurantTodayUserChoiceLiveData.setValue(true);

        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals(R.drawable.restaurant_icon_white, model.getFabIcon());
        assertEquals(R.color.green, model.getFabColor());

    }

    @Test
    public void shouldReturnCorrectPhoneNumberBlockDisplay() throws InterruptedException {
        //Given
        givenRoutine();
        detailResult = new DetailResult(
                "testName",
                Arrays.asList(
                        new AddressComponent("6"),
                        new AddressComponent("rue du Test")),
                null,
                Arrays.asList(
                        new Photos("photoref1"),
                        new Photos("photoref2")),
                "www.test.com",
                "1234",
                new OpeningHours(true),
                new Geometry(new Location(10.00, 10.00))
        );
        placeDetailLiveData.setValue(detailResult);


        //When
        viewModel.fetchRestaurantInfo("1234");
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertNull(null, model.getPhoneNumber());
        assertEquals(false, model.getPhoneClickable());
        assertEquals(R.color.missingInfoColor, model.getPhoneColor());

        //Given
        detailResult = new DetailResult(
                "testName",
                Arrays.asList(
                        new AddressComponent("6"),
                        new AddressComponent("rue du Test")),
                "0707070707",
                Arrays.asList(
                        new Photos("photoref1"),
                        new Photos("photoref2")),
                "www.test.com",
                "1234",
                new OpeningHours(true),
                new Geometry(new Location(10.00, 10.00))
        );
        placeDetailLiveData.setValue(detailResult);


        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals("0707070707", model.getPhoneNumber());
        assertEquals(true, model.getPhoneClickable());
        assertEquals(R.color.colorPrimary, model.getPhoneColor());
    }

    @Test
    public void shouldReturnCorrectWebSiteBlockDisplay() throws InterruptedException {
        //Given
        givenRoutine();
        detailResult = new DetailResult(
                "testName",
                Arrays.asList(
                        new AddressComponent("6"),
                        new AddressComponent("rue du Test")),
                null,
                Arrays.asList(
                        new Photos("photoref1"),
                        new Photos("photoref2")),
                null,
                "1234",
                new OpeningHours(true),
                new Geometry(new Location(10.00, 10.00))
        );
        placeDetailLiveData.setValue(detailResult);


        //When
        viewModel.fetchRestaurantInfo("1234");
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertNull(null, model.getWebSiteUrl());
        assertEquals(false, model.getWebSiteClickable());
        assertEquals(R.color.missingInfoColor, model.getWebSiteColor());

        //Given
        detailResult = new DetailResult(
                "testName",
                Arrays.asList(
                        new AddressComponent("6"),
                        new AddressComponent("rue du Test")),
                "0707070707",
                Arrays.asList(
                        new Photos("photoref1"),
                        new Photos("photoref2")),
                "www.test.com",
                "1234",
                new OpeningHours(true),
                new Geometry(new Location(10.00, 10.00))
        );
        placeDetailLiveData.setValue(detailResult);


        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals("www.test.com", model.getWebSiteUrl());
        assertEquals(true, model.getWebSiteClickable());
        assertEquals(R.color.colorPrimary, model.getWebSiteColor());
    }

    @Test
    public void shouldReturnCorrectLikeBlockDisplay() throws InterruptedException {
        //Given
        givenRoutine();
        isRestaurantLikedByUserLiveData.setValue(false);

        //When
        viewModel.fetchRestaurantInfo("1234");
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals(R.drawable.ic_star_bordered, model.getLikeIcon());

        //Given
        givenRoutine();
        isRestaurantLikedByUserLiveData.setValue(true);

        //When
        model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());

        //Then
        assertEquals(R.drawable.ic_star_yellow, model.getLikeIcon());
    }

    private void givenRoutine() {
        detailResult = getMockDetailResult();
        placeDetailLiveData.setValue(detailResult);
        isRestaurantLikedByUserLiveData.setValue(true);
        restaurantLikeCountLiveData.setValue(1);
        isRestaurantTodayUserChoiceLiveData.setValue(true);
        List<FireStoreUser> users = Arrays.asList(
                new FireStoreUser("testeur1ID", "testeur1", "www.photoTesteur1Avatar.com"),
                new FireStoreUser("testeur2ID", "testeur2", "www.photoTesteur2Avatar.com")
        );
        todayListOfUsersLiveData.setValue(users);
        when(context.getString(R.string.eats_here)).thenReturn(" eats here");
    }


    private DetailResult getMockDetailResult() {
        return new DetailResult(
                "testName",
                Arrays.asList(
                        new AddressComponent("6"),
                        new AddressComponent("rue du Test")),
                "0606060606",
                Arrays.asList(
                        new Photos("photoref1"),
                        new Photos("photoref2")),
                "www.test.com",
                "1234",
                new OpeningHours(true),
                new Geometry(new Location(10.00, 10.00))
        );
    }
}