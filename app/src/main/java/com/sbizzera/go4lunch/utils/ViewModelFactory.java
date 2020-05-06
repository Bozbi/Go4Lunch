package com.sbizzera.go4lunch.utils;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.main_activity.MainActivityViewModel;
import com.sbizzera.go4lunch.main_activity.fragments.list_fragment.ListFragmentViewModel;
import com.sbizzera.go4lunch.main_activity.fragments.map_fragment.MapFragmentViewModel;
import com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment.WorkmatesFragmentViewModel;
import com.sbizzera.go4lunch.notification.WorkManagerHelper;
import com.sbizzera.go4lunch.restaurant_activity.RestaurantViewModel;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;
import com.sbizzera.go4lunch.services.AuthService;
import com.sbizzera.go4lunch.services.CurrentGPSLocationRepo;
import com.sbizzera.go4lunch.services.FireStoreRepo;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.services.SortTypeChosenRepo;
import com.sbizzera.go4lunch.services.VisibleRegionRepo;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    private ViewModelFactory() {
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory();
                }
            }
        }
        return sFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapFragmentViewModel.class)) {
            return (T) new MapFragmentViewModel(
                    CurrentGPSLocationRepo.getInstance(LocationServices.getFusedLocationProviderClient(App.getApplication())),
                    GooglePlacesService.getInstance(),
                    FireStoreRepo.getInstance(),
                    VisibleRegionRepo.getInstance(),
                    PermissionService.getInstance()
            );
        }
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(
                    GooglePlacesService.getInstance(),
                    FireStoreRepo.getInstance(),
                    AuthService.getInstance(FirebaseAuth.getInstance(),AuthUI.getInstance()),
                    App.getApplication()
            );
        }
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(
                    FireStoreRepo.getInstance(),
                    SharedPreferencesRepo.getInstance(App.getApplication(),WorkManagerHelper.getInstance(App.getApplication())),
                    VisibleRegionRepo.getInstance(),
                    PermissionService.getInstance(),
                    WorkManagerHelper.getInstance(App.getApplication()),
                    AuthService.getInstance(FirebaseAuth.getInstance(), AuthUI.getInstance()),
                    App.getApplication()
            );
        }
        if (modelClass.isAssignableFrom(WorkmatesFragmentViewModel.class)) {
            return (T) new WorkmatesFragmentViewModel(
                    FireStoreRepo.getInstance(),
                    App.getApplication()
                    );
        }
        if (modelClass.isAssignableFrom(ListFragmentViewModel.class)) {
            return (T) new ListFragmentViewModel(
                    CurrentGPSLocationRepo.getInstance(LocationServices.getFusedLocationProviderClient(App.getApplication())),
                    GooglePlacesService.getInstance(),
                    FireStoreRepo.getInstance(),
                    SortTypeChosenRepo.getInstance(),
                    App.getApplication()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
