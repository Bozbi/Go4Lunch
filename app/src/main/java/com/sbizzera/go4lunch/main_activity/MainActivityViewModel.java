package com.sbizzera.go4lunch.main_activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.sbizzera.go4lunch.main_activity.your_lunch_dialog.YourLunchModel;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreUser;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.services.VisibleRegionRepo;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private FireStoreService mFireStoreService;
    private SharedPreferencesRepo sharedPreferencesRepo;

    private MediatorLiveData<MainActivityModel> modelLD = new MediatorLiveData<>();
    private SingleLiveEvent<ViewAction> mActionLE = new SingleLiveEvent<>();
    private SingleLiveEvent<RectangularBounds> mViewActionSearch = new SingleLiveEvent<>();
    private SingleLiveEvent<YourLunchModel> mViewActionYourLunch = new SingleLiveEvent<>();
    private MediatorLiveData<String> dummyMediator;

    private VisibleRegionRepo mVisibleRegionRepo;
    private String mCurrentAutocompleteRestaurantID;
    private LiveData<FireStoreLunch> userTodayLunchLD;

    private LiveData<List<FireStoreUser>> joiningWorkmatesLD;


    public MainActivityViewModel(
            FireStoreService mFireStoreService,
            SharedPreferencesRepo sharedPreferencesRepo,
            VisibleRegionRepo visibleRegionRepo,
            PermissionService permissionService
    ) {
        this.mFireStoreService = mFireStoreService;
        this.sharedPreferencesRepo = sharedPreferencesRepo;
        mVisibleRegionRepo = visibleRegionRepo;
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
        LiveData<Boolean> isNotificationOnLD = sharedPreferencesRepo.getNotificationPreferencesLiveData();
        modelLD.addSource(isNotificationOnLD, this::combineSources);

        //Wire userLunch and other users
        userTodayLunchLD = mFireStoreService.getUserLunch();
        joiningWorkmatesLD = Transformations.switchMap(userTodayLunchLD, userTodayLunch -> {
            if (userTodayLunch == null) {
                return mFireStoreService.getTodayListOfUsers(null);
            }
            return mFireStoreService.getTodayListOfUsers(userTodayLunch.getRestaurantId());
        });

        modelLD.addSource(userTodayLunchLD, userTodayLunch -> {
        });
        modelLD.addSource(joiningWorkmatesLD, joiningWorkmates -> {
        });
    }

    public MediatorLiveData<String> getDummyMediator() {
        return dummyMediator;
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
        mFireStoreService.updateUserInDb();
    }

    public SingleLiveEvent<RectangularBounds> getViewActionSearch() {
        return mViewActionSearch;
    }

    public SingleLiveEvent<ViewAction> getActionLE() {
        return mActionLE;
    }

    public void showAutocomplete() {
        mViewActionSearch.setValue(RectangularBounds.newInstance(mVisibleRegionRepo.getLastMapVisibleRegion().getValue().latLngBounds));
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

    public void getYourLunchDialog() {
        FireStoreLunch lunch = userTodayLunchLD.getValue();
        List<FireStoreUser> joiningWorkmates = joiningWorkmatesLD.getValue();
        YourLunchModel model = combineYourLunchSources(lunch, joiningWorkmates);
        mViewActionYourLunch.setValue(model);
    }

    private YourLunchModel combineYourLunchSources(FireStoreLunch lunch, List<FireStoreUser> joiningWorkmates) {
        boolean shouldPositiveBtnBeAvailable = false;
        String restaurantId = "";
        String dialogText = createDialogText(lunch, joiningWorkmates);
        if (lunch != null && lunch.getRestaurantId() != null) {
            shouldPositiveBtnBeAvailable = true;
            restaurantId = lunch.getRestaurantId();
        }

        return new YourLunchModel(dialogText, shouldPositiveBtnBeAvailable, restaurantId);
    }

    private String createDialogText(FireStoreLunch lunch, List<FireStoreUser> joiningWorkmates) {
        if (joiningWorkmates != null) {
            FireStoreUser userToRemove = new FireStoreUser();
            for (FireStoreUser user : joiningWorkmates) {
                if (user.getUserId().equals(FirebaseAuthService.getUser().getUid())) {
                    userToRemove = user;
                }
            }
            if (userToRemove.getUserId() != null) {
                joiningWorkmates.remove(userToRemove);
            }
        }

        String dialogText;
        if (lunch == null || lunch.getRestaurantName() == null) {
            dialogText = "Hey %User%,\nYou have'nt shared your choice today !\nDo it now so workmates can join you !";
        } else {
            dialogText = "Hey %User%,\nToday you're eating at %Restaurant%%Workmates%.\nHave a good time!";
            dialogText = dialogText.replace("%Restaurant%", lunch.getRestaurantName());
            if (joiningWorkmates != null) {
                if (joiningWorkmates.size() == 0) {
                    dialogText = dialogText.replace("%Workmates%", "");
                }
                if (joiningWorkmates.size() == 1) {
                    String workmateText = " with " + Go4LunchUtils.getUserFirstName(joiningWorkmates.get(0).getUserName());
                    dialogText = dialogText.replace("%Workmates%", workmateText);
                } else {
                    StringBuilder workmatesTextBuilder = new StringBuilder();
                    workmatesTextBuilder.append(" with ");
                    for (int i = 0; i < joiningWorkmates.size(); i++) {

                        if (i == joiningWorkmates.size() - 1) {
                            workmatesTextBuilder.append(" and ");
                            workmatesTextBuilder.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i).getUserName()));

                        }else if(i ==joiningWorkmates.size()-2){
                            workmatesTextBuilder.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i).getUserName()));
                        } else {
                            workmatesTextBuilder.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i).getUserName()));
                            workmatesTextBuilder.append(", ");
                        }
                    }
                    dialogText = dialogText.replace("%Workmates%",workmatesTextBuilder.toString());
                }
            }
        }
        dialogText = dialogText.replace("%User%", FirebaseAuthService.getUserFirstName());

        return dialogText;

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

    public SingleLiveEvent<YourLunchModel> getViewActionYourLunch() {
        return mViewActionYourLunch;
    }
}
