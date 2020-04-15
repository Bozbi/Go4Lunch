package com.sbizzera.go4lunch.services;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.main_activity.MainActivityViewModel;
import com.sbizzera.go4lunch.main_activity.fragments.list_fragment.ListFragmentViewModel;
import com.sbizzera.go4lunch.main_activity.fragments.map_fragment.MapFragmentViewModel;
import com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment.WorkmatesFragmentViewModel;
import com.sbizzera.go4lunch.main_activity.your_lunch_dialog.YourLunchDialogViewModel;
import com.sbizzera.go4lunch.restaurant_activity.RestaurantViewModel;
import com.sbizzera.go4lunch.notification.SharedPreferencesRepo;

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


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapFragmentViewModel.class)) {
            return (T) new MapFragmentViewModel(
                   new LocationService(App.getApplication()),
                    GooglePlacesService.getInstance(),
                    new FireStoreService(),
                    PermissionService.getInstance(),
                    CameraPositionRepo.getInstance()
            );
        }
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(
                    GooglePlacesService.getInstance(),
                    new FireStoreService()
            );
        }
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(new FireStoreService(),new SharedPreferencesRepo());
        }
        if (modelClass.isAssignableFrom(WorkmatesFragmentViewModel.class)){
            return (T)new WorkmatesFragmentViewModel(new FireStoreService());
        }
        if (modelClass.isAssignableFrom(ListFragmentViewModel.class)){
            return (T)new ListFragmentViewModel(
                    new LocationService(App.getApplication()),
                    GooglePlacesService.getInstance(),
                    new FireStoreService()
            );
        }
        if (modelClass.isAssignableFrom(YourLunchDialogViewModel.class)){
            return (T)new YourLunchDialogViewModel(new FireStoreService());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
