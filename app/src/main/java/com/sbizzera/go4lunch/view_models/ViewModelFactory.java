package com.sbizzera.go4lunch.view_models;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.services.DeviceLocator;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.PermissionHandler;
import com.sbizzera.go4lunch.services.RestaurantRepository;
import com.sbizzera.go4lunch.views.fragments.ListFragment;

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
                    new DeviceLocator(App.getApplication()),
                    PermissionHandler.getInstance(),
                    RestaurantRepository.getInstance()
            );
        }
        if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(
                    RestaurantRepository.getInstance(),
                    new FireStoreService()
            );
        }
        if (modelClass.isAssignableFrom(ListRestaurantViewModel.class)) {
            return (T) new ListRestaurantViewModel(new FireStoreService());
        }
        if (modelClass.isAssignableFrom(WorkmatesFragmentViewModel.class)){
            return (T)new WorkmatesFragmentViewModel(new FireStoreService());
        }
        if (modelClass.isAssignableFrom(ListFragmentViewModel.class)){
            return (T)new ListFragmentViewModel(
                    new DeviceLocator(App.getApplication()),
                    PermissionHandler.getInstance(),
                    RestaurantRepository.getInstance(),
                    new FireStoreService()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
