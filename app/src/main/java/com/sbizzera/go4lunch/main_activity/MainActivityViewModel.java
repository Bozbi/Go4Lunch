package com.sbizzera.go4lunch.main_activity;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.main_activity.fragments.NoPermissionFragment;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.utils.SingleLiveEvent;

public class MainActivityViewModel extends ViewModel {

    private FireStoreService fireStore;
    private SharedPreferencesRepo sharedPreferencesRepo;
    private MediatorLiveData<MainActivityModel> modelLD = new MediatorLiveData<>();
    private SingleLiveEvent<ViewAction> actionLEvent = new SingleLiveEvent<>();


    public MainActivityViewModel(FireStoreService fireStore, SharedPreferencesRepo sharedPreferencesRepo) {
        this.fireStore = fireStore;
        this.sharedPreferencesRepo = sharedPreferencesRepo;
        updateUserInDb();
        wireUp();
        WorkManagerHelper.handleNotificationWork();
    }

    private void wireUp() {
        LiveData<Boolean> isNotificationOnLD = sharedPreferencesRepo.getNotificationPreferencesLiveData();
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

    SingleLiveEvent<ViewAction> getActionLEvent() {
        return actionLEvent;
    }


    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(App.getApplication(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            actionLEvent.setValue(ViewAction.DISPLAY_PERMISSION_DIALOG);
        }
    }

    public enum ViewAction {
        DISPLAY_PERMISSION_DIALOG,
    }
}
