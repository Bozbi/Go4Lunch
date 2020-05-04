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
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.your_lunch_dialog.YourLunchModel;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreUser;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.FirebaseUserRepo;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.services.ResourcesProvider;
import com.sbizzera.go4lunch.services.VisibleRegionRepo;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;

import java.util.List;

import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {

    private FireStoreService mFireStoreService;
    private SharedPreferencesRepo sharedPreferencesRepo;
    private ResourcesProvider mResourcesProvider;


    private MediatorLiveData<MainActivityModel> modelLD = new MediatorLiveData<>();
    private SingleLiveEvent<ViewAction> mActionLE = new SingleLiveEvent<>();
    private SingleLiveEvent<RectangularBounds> mViewActionSearch = new SingleLiveEvent<>();
    private SingleLiveEvent<YourLunchModel> mViewActionYourLunch = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mViewActionLaunchRestaurantDetailsLE = new SingleLiveEvent<>();

    private VisibleRegionRepo mVisibleRegionRepo;
    private LiveData<FireStoreLunch> userTodayLunchLD;
    private LiveData<List<FireStoreUser>> joiningWorkmatesLD;


    public MainActivityViewModel(
            FireStoreService mFireStoreService,
            SharedPreferencesRepo sharedPreferencesRepo,
            VisibleRegionRepo visibleRegionRepo,
            PermissionService permissionService,
            ResourcesProvider resourcesProvider
    ) {
        this.mFireStoreService = mFireStoreService;
        this.sharedPreferencesRepo = sharedPreferencesRepo;
        mVisibleRegionRepo = visibleRegionRepo;
        mResourcesProvider = resourcesProvider;
        updateUserInDb();
        wireUp();
        // TODO BOZBI Pas de static mais plutôt singleton + injection pour tester ce comportement en TU
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

        // TODO BOZBI SINGLE LIVE EVENT + MEDIATOR Utilise un SingleLiveEvent qui extends d'un Mediator plutôt d'une LiveData
        //  Ca te permettra d'éviter ce bricolage ici et d'être explicite sur tes "liens" entre tes livedatas
        //addding to model soruce so livedata warms up
        modelLD.addSource(userTodayLunchLD, userTodayLunch -> {
        });
        modelLD.addSource(joiningWorkmatesLD, joiningWorkmates -> {
        });
    }


    private void combineSources(Boolean isNotificationOn) {

        String userPhotoUrl = FirebaseAuthService.getUserPhotoUrl();
        String userName = FirebaseAuthService.getUserName();
        String userEmail = FirebaseAuthService.getUserEmail();
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

    public void getYourLunchDialog() {
        // TODO BOZBI SINGLE LIVE EVENT + MEDIATOR Ne jamais faire de .getValue() (à part lors d'un addSource ofc)
        //  Utiliser un Mediator permet d'être dynamique
        FireStoreLunch lunch = userTodayLunchLD.getValue();
        List<FireStoreUser> joiningWorkmates = joiningWorkmatesLD.getValue();
        // TODO BOZBI SINGLE LIVE EVENT + MEDIATOR Tu as déjà la moitié faite ici, tu avais la bonne idée, bravo !
        YourLunchModel model = combineYourLunchSources(lunch, joiningWorkmates);
        mViewActionYourLunch.setValue(model);
    }

    private YourLunchModel combineYourLunchSources(FireStoreLunch lunch, List<FireStoreUser> joiningWorkmates) {
        boolean shouldPositiveBtnBeAvailable = false;
        // TODO BOZBI Accepte le null, propager une chaine vide à la place d'une valeur null ne fait que déplacer le problème
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

        // TODO BOZBI STRING FORMAT Alternative : utilise Context.getString(int, Object...) pour formatter ton texte plus simplement
        //  https://github.com/NinoDLC/MVVM_Clean_Archi_Java/blob/master/app/src/main/java/fr/delcey/mvvm_clean_archi_java/view/MainViewModel.java#L229
        //  https://github.com/NinoDLC/MVVM_Clean_Archi_Java/blob/master/app/src/main/res/values/strings.xml#L5
        String dialogText ;
        if (lunch == null || lunch.getRestaurantName() == null) {
            dialogText= mResourcesProvider.getDialogTextNoChoice();
        } else {
            dialogText =mResourcesProvider.getDialogTextWithChoice();
            dialogText = dialogText.replace("%Restaurant%", lunch.getRestaurantName());
            if (joiningWorkmates != null) {
                if (joiningWorkmates.size() == 0) {
                    dialogText = dialogText.replace("%Workmates%", "");
                }
                if (joiningWorkmates.size() == 1) {
                    String withStr = mResourcesProvider.getDialogTextWith();
                    String workmateText = withStr + Go4LunchUtils.getUserFirstName(joiningWorkmates.get(0).getUserName());
                    dialogText = dialogText.replace("%Workmates%", workmateText);
                } else {
                    StringBuilder workmatesTextBuilder = new StringBuilder();
                    workmatesTextBuilder.append(mResourcesProvider.getDialogTextWith());
                    for (int i = 0; i < joiningWorkmates.size(); i++) {

                        if (i == joiningWorkmates.size() - 1) {
                            workmatesTextBuilder.append(mResourcesProvider.getDialogTextAnd());
                            workmatesTextBuilder.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i).getUserName()));

                        } else if (i == joiningWorkmates.size() - 2) {
                            workmatesTextBuilder.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i).getUserName()));
                        } else {
                            workmatesTextBuilder.append(Go4LunchUtils.getUserFirstName(joiningWorkmates.get(i).getUserName()));
                            workmatesTextBuilder.append(", ");
                        }
                    }
                    dialogText = dialogText.replace("%Workmates%", workmatesTextBuilder.toString());
                }
            }
        }
        dialogText = dialogText.replace("%User%", FirebaseAuthService.getUserFirstName());

        return dialogText;

    }


    enum ViewAction {
        SHOW_NOT_A_RESTAURANT_TOAST,
        ASK_LOCATION_PERMISSION
    }


    public SingleLiveEvent<YourLunchModel> getViewActionYourLunch() {
        return mViewActionYourLunch;
    }

    public SingleLiveEvent<String> getmViewActionLaunchRestaurantDetailsLE() {
        return mViewActionLaunchRestaurantDetailsLE;
    }
}
