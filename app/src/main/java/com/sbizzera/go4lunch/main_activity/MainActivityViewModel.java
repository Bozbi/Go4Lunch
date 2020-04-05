package com.sbizzera.go4lunch.main_activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;

public class MainActivityViewModel extends ViewModel {

    private FireStoreService fireStore;
    private PermissionService permissionService;
    private LocationService locationService;
    private SharedPreferencesRepo sharedPreferencesRepo;
    private MediatorLiveData<MainActivityModel> modelLD = new MediatorLiveData<>();
    private FirebaseUser user = FirebaseAuthService.getUser();


    public MainActivityViewModel(FireStoreService fireStore, PermissionService permissionService, LocationService locationService, SharedPreferencesRepo sharedPreferencesRepo) {
        this.fireStore = fireStore;
        this.permissionService = permissionService;
        this.locationService = locationService;
        this.sharedPreferencesRepo = sharedPreferencesRepo;
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
        if(isNotificationOn){
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

    ;

    public void updateSharedPrefs(Boolean isChecked){
        sharedPreferencesRepo.saveNotificationPreferences(isChecked);
    }

    public void updateUserInDb() {
        fireStore.updateUserInDb();
    }
}
