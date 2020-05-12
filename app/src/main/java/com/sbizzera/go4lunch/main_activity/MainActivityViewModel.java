package com.sbizzera.go4lunch.main_activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.models.MainActivityModel;
import com.sbizzera.go4lunch.repositories.PermissionRepo;
import com.sbizzera.go4lunch.repositories.SharedPreferencesRepo;
import com.sbizzera.go4lunch.repositories.VisibleRegionRepo;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreLunch;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreUser;
import com.sbizzera.go4lunch.services.AuthHelper;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;
import com.sbizzera.go4lunch.utils.SingleLiveEventMediator;
import com.sbizzera.go4lunch.your_lunch_dialog.models.YourLunchModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private FireStoreRepo mFireStoreRepo;
    private SharedPreferencesRepo mSharedPreferencesRepo;
    private Context mContext;
    private AuthHelper mAuthHelper;


    private MediatorLiveData<MainActivityModel> modelLD = new MediatorLiveData<>();
    private SingleLiveEvent<ViewAction> mActionLE = new SingleLiveEvent<>();
    private SingleLiveEvent<RectangularBounds> mViewActionSearch = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mViewActionLaunchRestaurantDetailsLE = new SingleLiveEvent<>();

    private VisibleRegionRepo mVisibleRegionRepo;
    private LiveData<FireStoreLunch> userTodayLunchLD;
    private LiveData<List<FireStoreUser>> joiningWorkmatesLD;
    private SingleLiveEventMediator<YourLunchModel> yourLunchMLD = new SingleLiveEventMediator<>();
    private MutableLiveData<Boolean> dialogButtonClickLD = new MutableLiveData<>();


    public MainActivityViewModel(
            FireStoreRepo fireStoreRepo,
            SharedPreferencesRepo sharedPreferencesRepo,
            VisibleRegionRepo visibleRegionRepo,
            PermissionRepo permissionRepo,
            AuthHelper authHelper,
            Context context
    ) {
        mFireStoreRepo = fireStoreRepo;
        mSharedPreferencesRepo = sharedPreferencesRepo;
        mVisibleRegionRepo = visibleRegionRepo;
        mAuthHelper = authHelper;
        mContext = context;
        sharedPreferencesRepo.updateLiveData(mAuthHelper.getUser().getUid());
        updateUserInDb();
        wireUp();
        checkForLocationPermissions(permissionRepo);
    }

    private void checkForLocationPermissions(@NonNull PermissionRepo permissionRepo) {
        if (!permissionRepo.hasPermissionBeenAsked() && !permissionRepo.isLocationPermissionGranted()) {
            permissionRepo.setHasPermissionBeenAsked(true);
            mActionLE.setValue(ViewAction.ASK_LOCATION_PERMISSION);
        }
    }

    private void wireUp() {
        LiveData<Boolean> isNotificationOnLD = mSharedPreferencesRepo.getNotificationPreferencesLiveData();
        modelLD.addSource(isNotificationOnLD, this::combineSources);

        //Wire userLunch and other users
        userTodayLunchLD = mFireStoreRepo.getUserLunch(mAuthHelper.getUser().getUid());
        joiningWorkmatesLD = Transformations.switchMap(userTodayLunchLD, userTodayLunch -> {
            if (userTodayLunch == null) {
                return mFireStoreRepo.getTodayListOfUsers(null);
            }
            return mFireStoreRepo.getTodayListOfUsers(userTodayLunch.getRestaurantId());
        });

        yourLunchMLD.addSource(userTodayLunchLD, userTodayLunch -> {
            combineYourLunchSources(userTodayLunch, joiningWorkmatesLD.getValue(), dialogButtonClickLD.getValue());
        });
        yourLunchMLD.addSource(joiningWorkmatesLD, joiningWorkmates -> {
            combineYourLunchSources(userTodayLunchLD.getValue(), joiningWorkmates, dialogButtonClickLD.getValue());
        });

        yourLunchMLD.addSource(dialogButtonClickLD, clicked -> {
            combineYourLunchSources(userTodayLunchLD.getValue(), joiningWorkmatesLD.getValue(), clicked);
        });
    }


    private void combineSources(Boolean isNotificationOn) {

        String userPhotoUrl = mAuthHelper.getUserPhotoUrl();
        String userName = mAuthHelper.getUserName();
        String userEmail = mAuthHelper.getUserEmail();
        String switchText = "OFF";
        if (isNotificationOn) {
            switchText = "ON";
        }

        modelLD.setValue(new MainActivityModel(
                userPhotoUrl,
                userName,
                userEmail,
                isNotificationOn,
                switchText
        ));
    }

    public LiveData<MainActivityModel> getModel() {
        return modelLD;
    }

    void updateSharedPrefs(Boolean isChecked) {
        mSharedPreferencesRepo.saveNotificationPreferences(isChecked, mAuthHelper.getUser().getUid());
    }

    private void updateUserInDb() {
        mFireStoreRepo.updateUserInDb(mAuthHelper.getUser());
    }

    public SingleLiveEvent<RectangularBounds> getViewActionSearch() {
        return mViewActionSearch;
    }

    public SingleLiveEvent<ViewAction> getActionLE() {
        return mActionLE;
    }

    public void showAutocomplete() {
        if (mVisibleRegionRepo.getLastMapVisibleRegion().getValue() != null) {
            mViewActionSearch.setValue(RectangularBounds.newInstance(mVisibleRegionRepo.getLastMapVisibleRegion().getValue().latLngBounds));
        }
    }

    public void onAutocompleteClick(Intent data) {
        Place place = Autocomplete.getPlaceFromIntent(data);
        List<Place.Type> types = place.getTypes();
        if (types != null && types.contains(Place.Type.RESTAURANT)) {
            mViewActionLaunchRestaurantDetailsLE.setValue(place.getId());
        } else {
            mActionLE.setValue(ViewAction.SHOW_NOT_A_RESTAURANT_TOAST);
        }
    }

    public void yourLunchButtonClicked() {
        dialogButtonClickLD.setValue(true);
    }

    private void combineYourLunchSources(FireStoreLunch lunch, List<FireStoreUser> joiningWorkmates, Boolean clicked) {
        if (clicked == null || !clicked) {
            return;
        }

        boolean shouldPositiveBtnBeAvailable = false;
        String restaurantId = null;
        String dialogText = createDialogText(lunch, joiningWorkmates);
        if (lunch != null && lunch.getRestaurantId() != null) {
            shouldPositiveBtnBeAvailable = true;
            restaurantId = lunch.getRestaurantId();
        }
        yourLunchMLD.setValue(new YourLunchModel(dialogText, shouldPositiveBtnBeAvailable, restaurantId));
        dialogButtonClickLD.setValue(false);
    }

    private String createDialogText(FireStoreLunch lunch, List<FireStoreUser> joiningWorkmates) {
        joiningWorkmates = removeCurrentUserFromList(joiningWorkmates);
        List<String> joiningWorkmatesStr = getFirstNames(joiningWorkmates);
        String restaurantName = null;
        if (lunch != null) {
            restaurantName = lunch.getRestaurantName();
        }
        String joiningWorkmatesString = createJoiningWorkmatesString(joiningWorkmatesStr);
        return getDialogText(mAuthHelper.getUserFirstName(), restaurantName, joiningWorkmatesString);
    }

    private String getDialogText(String userFirstName, String restaurantName, String joiningWorkmatesString) {
        if (restaurantName == null) {
            return mContext.getString(R.string.dialog_text_no_choice, userFirstName);
        } else if (joiningWorkmatesString == null) {
            return mContext.getString(R.string.dialog_text_with_choice, userFirstName, restaurantName, "");
        }
        return mContext.getString(R.string.dialog_text_with_choice, userFirstName, restaurantName, joiningWorkmatesString);
    }


    private List<String> getFirstNames(List<FireStoreUser> joiningWorkmates) {
        List<String> listToReturn = new ArrayList<>();
        if (joiningWorkmates != null) {
            for (FireStoreUser user : joiningWorkmates) {
                listToReturn.add(Go4LunchUtils.getUserFirstName(user.getUserName()));
            }
        }
        return listToReturn;
    }

    private String createJoiningWorkmatesString(List<String> joiningWorkmatesStr) {
        String stringToReturn = null;
        if (joiningWorkmatesStr != null && joiningWorkmatesStr.size() != 0) {
            stringToReturn = mContext.getString(R.string.dialog_text_with);
            if (joiningWorkmatesStr.size() == 1) {
                stringToReturn = stringToReturn + joiningWorkmatesStr.get(0);
            } else {
                for (int i = 0; i < joiningWorkmatesStr.size(); i++) {
                    if (i != joiningWorkmatesStr.size() - 1) {
                        stringToReturn = stringToReturn + joiningWorkmatesStr.get(i) + ", ";
                    } else {
                        stringToReturn = stringToReturn + mContext.getString(R.string.dialog_text_and) + joiningWorkmatesStr.get(i);

                    }
                }
            }
        }
        return stringToReturn;
    }

    private List<FireStoreUser> removeCurrentUserFromList(List<FireStoreUser> joiningWorkmates) {
        if (joiningWorkmates != null) {
            FireStoreUser userToRemove = new FireStoreUser();
            for (FireStoreUser user : joiningWorkmates) {
                if (user.getUserId().equals(mAuthHelper.getUser().getUid())) {
                    userToRemove = user;
                }
            }
            if (userToRemove.getUserId() != null) {
                joiningWorkmates.remove(userToRemove);
            }
        }
        return joiningWorkmates;
    }

    public void logOutUser() {
        mAuthHelper.logOut(mContext).addOnCompleteListener(task -> {
            mActionLE.setValue(ViewAction.LOG_OUT);
        });
    }


    enum ViewAction {
        SHOW_NOT_A_RESTAURANT_TOAST,
        ASK_LOCATION_PERMISSION,
        LOG_OUT
    }


    public SingleLiveEventMediator<YourLunchModel> getViewActionYourLunch() {
        return yourLunchMLD;
    }

    public SingleLiveEvent<String> getmViewActionLaunchRestaurantDetailsLE() {
        return mViewActionLaunchRestaurantDetailsLE;
    }
}
