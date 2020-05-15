package com.sbizzera.go4lunch.main_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.ListenableWorker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.LiveDataTestUtil;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.models.MainActivityModel;
import com.sbizzera.go4lunch.repositories.PermissionRepo;
import com.sbizzera.go4lunch.repositories.SharedPreferencesRepo;
import com.sbizzera.go4lunch.repositories.VisibleRegionRepo;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreLunch;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreUser;
import com.sbizzera.go4lunch.services.AuthHelper;
import com.sbizzera.go4lunch.your_lunch_dialog.models.YourLunchModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.meta.When;

import timber.log.Timber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityViewModelTest {

    @Mock
    public Context context;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PermissionRepo permissionRepo;
    private AuthHelper authHelper;

    private MutableLiveData<Boolean> notificationStatusLD = new MutableLiveData<>();
    private MutableLiveData<FireStoreLunch> userTodayLunchLD = new MutableLiveData<>();
    private MutableLiveData<List<FireStoreUser>> joiningWorkmatesLD = new MutableLiveData<>();

    private MainActivityViewModel viewModel;

    @Before
    public void setUp() throws Exception {


        FireStoreRepo fireStoreRepo = mock(FireStoreRepo.class);
        SharedPreferencesRepo sharedPreferencesRepo = mock(SharedPreferencesRepo.class);
        VisibleRegionRepo visibleRegionRepo = mock(VisibleRegionRepo.class);
        permissionRepo = mock(PermissionRepo.class);
        authHelper = mock(AuthHelper.class);
        FirebaseUser user = mock(FirebaseUser.class);
        doReturn("testId").when(user).getUid();


        doReturn(notificationStatusLD).when(sharedPreferencesRepo).getNotificationPreferencesLiveData();
        doReturn(userTodayLunchLD).when(fireStoreRepo).getUserLunch("testId");
        doReturn(joiningWorkmatesLD).when(fireStoreRepo).getTodayListOfUsers("TestId");


//        when(context.getString(R.string.dialog_text_with_choice)).thenReturn("dialog with choice %s");
//        when(context.getString(R.string.dialog_text_no_choice)).thenReturn("dialog no choice %s");
//        when(context.getString(R.string.dialog_text_with)).thenReturn(" avec ");
//        when(context.getString(R.string.dialog_text_and)).thenReturn(" et ");





        doReturn("userTestPhotoUrl").when(authHelper).getUserPhotoUrl();
        doReturn("userTestName").when(authHelper).getUserName();
        doReturn("userTestEmail").when(authHelper).getUserEmail();
        doReturn("userFirstName").when(authHelper).getUserFirstName();
        doReturn(user).when(authHelper).getUser();


        viewModel = new MainActivityViewModel(
                fireStoreRepo,
                sharedPreferencesRepo,
                visibleRegionRepo,
                permissionRepo,
                authHelper,
                context);



    }

    @Test
    public void shouldMapCorrectlyDataToMainActivityModel() throws InterruptedException {
        //Given
        notificationStatusLD.setValue(true);

        //When
        MainActivityModel mainActivityModel = LiveDataTestUtil.getOrAwaitValue(viewModel.getModel());

        //Then
        assertEquals("userTestPhotoUrl", mainActivityModel.getUserPhotoUrl());
        assertEquals("userTestEmail", mainActivityModel.getUserEmail());
        assertEquals("userTestName", mainActivityModel.getUserName());
        assertEquals(true, mainActivityModel.getNotificationOn());
        assertEquals("ON", mainActivityModel.getSwitchText());

        //Given
        notificationStatusLD.setValue(false);

        //When
        mainActivityModel = LiveDataTestUtil.getOrAwaitValue(viewModel.getModel());

        //Then
        assertEquals(false, mainActivityModel.getNotificationOn());
        assertEquals("OFF", mainActivityModel.getSwitchText());

    }

    @Test
    public void noLocationPermissionOnStartShouldFireViewActionAskLocationPermission() throws InterruptedException{
        //Given
        //TODO pourquoi il veut que je vire ces lignes
//        doReturn(false).when(permissionRepo).hasPermissionBeenAsked();
//        doReturn(false).when(permissionRepo).isLocationPermissionGranted();

        //When
        MainActivityViewModel.ViewAction viewAction = LiveDataTestUtil.getOrAwaitValue(viewModel.getActionLE());

        //Then
        assertEquals(MainActivityViewModel.ViewAction.ASK_LOCATION_PERMISSION,viewAction);

    }

//    @Test
//    public void logOutClickShouldFireViewActionLogOUt() throws InterruptedException{
//
//        viewModel.logOutUser();
//
//        //When
//        MainActivityViewModel.ViewAction viewAction = LiveDataTestUtil.getOrAwaitValue(viewModel.getActionLE());
//
//        //Then
//        assertEquals(MainActivityViewModel.ViewAction.LOG_OUT,viewAction);
//    }

    @Test
    public void getViewActionYourLunch() throws InterruptedException{
        userTodayLunchLD.setValue(new FireStoreLunch("mlsqkdf","mlsqkdj","mlsqkdf","TestId","mslqkdjf"));
        joiningWorkmatesLD.setValue(getUserList());
        viewModel.yourLunchButtonClicked();
        YourLunchModel yourLunchModel = LiveDataTestUtil.getOrAwaitValue(viewModel.getViewActionYourLunch());

        //TODO I can't get this to work get Dialog always null cause in CombineSources buttonClickGetsBackToFalse;
//        assertEquals("lksjdflkj",yourLunchModel.getDialogtext());
        assertEquals("TestId", yourLunchModel.getRestaurantId());
        assertTrue(yourLunchModel.isPositiveAvailable());

    }

    @Test
    public void clickOnAutocompleteNonRestaurantResultShouldNotFireYourLunchSingleLiveEvent() throws InterruptedException{
        ArrayList<Place.Type> types = new ArrayList<>();
        types.add(Place.Type.BOOK_STORE);
        viewModel.onAutocompleteClick(types,"restoId");

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("LiveData value was never set.");
        LiveDataTestUtil.getOrAwaitValue(viewModel.getmViewActionLaunchRestaurantDetailsLE());

    }

    @Test
    public void clickOnAutocompleteNonRestaurantResultShouldFireViewActionToastNotARestaurant() throws InterruptedException{
        ArrayList<Place.Type> types = new ArrayList<>();
        types.add(Place.Type.BOOK_STORE);
        viewModel.onAutocompleteClick(types,"restoId");

        MainActivityViewModel.ViewAction viewAction = LiveDataTestUtil.getOrAwaitValue(viewModel.getActionLE());

        assertEquals(MainActivityViewModel.ViewAction.SHOW_NOT_A_RESTAURANT_TOAST,viewAction);

    }
    @Test
    public void clickOnAutocompleteRestaurantResultShouldFireSingleLiveEventWithCorrectId() throws InterruptedException{
        ArrayList<Place.Type> types = new ArrayList<>();
        types.add(Place.Type.RESTAURANT);
        viewModel.onAutocompleteClick(types,"restoId");

        String restaurantId = LiveDataTestUtil.getOrAwaitValue(viewModel.getmViewActionLaunchRestaurantDetailsLE());

        assertEquals("restoId",restaurantId);

        types.add(Place.Type.BOOK_STORE);
        viewModel.onAutocompleteClick(types,"restoId2");

        restaurantId = LiveDataTestUtil.getOrAwaitValue(viewModel.getmViewActionLaunchRestaurantDetailsLE());
        assertEquals("restoId2",restaurantId);
    }


    private List<FireStoreUser> getUserList() {
        return Arrays.asList(
                new FireStoreUser("userTest2Id", "userTest2", "userTest2PhotoUrl"),
                new FireStoreUser("userTest3Id", "userTest3", "userTest3PhotoUrl"),
                new FireStoreUser("userTest4Id", "userTest4", "userTest4PhotoUrl")
        );
    }
}