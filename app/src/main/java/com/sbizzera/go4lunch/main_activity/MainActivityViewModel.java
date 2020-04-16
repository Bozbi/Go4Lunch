package com.sbizzera.go4lunch.main_activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.services.CameraPositionRepo;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private FireStoreService fireStore;
    private SharedPreferencesRepo sharedPreferencesRepo;

    private MediatorLiveData<MainActivityModel> modelLD = new MediatorLiveData<>();
    private SingleLiveEvent<ViewAction> mActionLE = new SingleLiveEvent<>();

    private SingleLiveEvent<RectangularBounds> mViewActionSearch = new SingleLiveEvent<>();

    private CameraPositionRepo mCameraPositionRepo;
    private LiveData<Boolean> isNotificationOnLD;
    private RectangularBounds mMapCurrentRectangularBounds;
    private String mCurrentAutocompleteRestaurantID;


    public MainActivityViewModel(
        FireStoreService fireStore,
        SharedPreferencesRepo sharedPreferencesRepo,
        CameraPositionRepo cameraPositionRepo,
        PermissionService permissionService
    ) {
        this.fireStore = fireStore;
        this.sharedPreferencesRepo = sharedPreferencesRepo;
        mCameraPositionRepo = cameraPositionRepo;
        updateUserInDb();
        wireUp();
        WorkManagerHelper.handleNotificationWork();

        checkForLocationPermissions(permissionService);
    }

    private void checkForLocationPermissions(@NonNull PermissionService permissionService) {
        if (!permissionService.hasPermissionBeenAsked() && !permissionService.isLocationPermissionGranted()) {
            permissionService.setPermissionBeenAsked(true);
            mActionLE.setValue(ViewAction.ASK_LOCATION_PERMISSION);
        }
    }

    private void wireUp() {
        isNotificationOnLD = sharedPreferencesRepo.getNotificationPreferencesLiveData();
        modelLD.addSource(isNotificationOnLD, this::combineSources);
    }

    private void combineSources(Boolean isNotificationOn) {

        String userPhotoUrl = FirebaseAuthService.getUserPhotoUrl();
        String userName = FirebaseAuthService.getUserName();
        String userEmail = FirebaseAuthService.getUserEmail();
        String toolBarTitle = "I'm Hungry";
        String switchText = "OFF";
        if (isNotificationOn) {
            switchText = "ON";
        }

        modelLD.setValue(new MainActivityModel(
                userPhotoUrl,
                userName,
                userEmail,
                toolBarTitle,
                isNotificationOn,
                switchText
        ));
    }

    public LiveData<MainActivityModel> getModel() {
        return modelLD;
    }

    void updateSharedPrefs(Boolean isChecked) {
        sharedPreferencesRepo.saveNotificationPreferences(isChecked);
    }

    private void updateUserInDb() {
        fireStore.updateUserInDb();
    }

    public SingleLiveEvent<RectangularBounds> getViewActionSearch() {
        return mViewActionSearch;
    }

    public SingleLiveEvent<ViewAction> getActionLE() {
        return mActionLE;
    }

    public void showAutocomplete() {
        mViewActionSearch.setValue( RectangularBounds.newInstance(mCameraPositionRepo.getLastVisibleRegion().latLngBounds));
    }

    public void onAutocompleteClick(Intent data) {
        Place place = Autocomplete.getPlaceFromIntent(data);
        List<Place.Type> types = place.getTypes();
        if (types != null && types.contains(Place.Type.RESTAURANT)) {
            mCurrentAutocompleteRestaurantID = place.getId();
            mActionLE.setValue(ViewAction.SHOW_RESTAURANT_DETAILS);
        } else {
            mActionLE.setValue(ViewAction.SHOW_NOT_A_RESTAURANT_TOAST);
        }
    }

    enum ViewAction {
        SHOW_RESTAURANT_DETAILS,
        SHOW_NOT_A_RESTAURANT_TOAST,
        ASK_LOCATION_PERMISSION
    }

    // TODO BORIS Same
    public String getCurrentAutocompleteRestaurantID() {
        return mCurrentAutocompleteRestaurantID;
    }
}
