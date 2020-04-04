package com.sbizzera.go4lunch.view_models;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.PermissionService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.SharedPreferencesRepo;

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
                    LocationService.getInstance(App.getApplication()),
                    PermissionService.getInstance(),
                    GooglePlacesService.getInstance(),
                    new FireStoreService()
            );
        }
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(
                    GooglePlacesService.getInstance(),
                    new FireStoreService()
            );
        }
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(new FireStoreService(), PermissionService.getInstance(),
                    LocationService.getInstance(App.getApplication()),new SharedPreferencesRepo());
        }
        if (modelClass.isAssignableFrom(WorkmatesFragmentViewModel.class)){
            return (T)new WorkmatesFragmentViewModel(new FireStoreService());
        }
        if (modelClass.isAssignableFrom(ListFragmentViewModel.class)){
            return (T)new ListFragmentViewModel(
                    LocationService.getInstance(App.getApplication()),
                    GooglePlacesService.getInstance(),
                    new FireStoreService()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
