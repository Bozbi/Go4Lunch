package com.sbizzera.go4lunch.view_models;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.services.DeviceLocator;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.PermissionHandler;
import com.sbizzera.go4lunch.services.RestaurantRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private static ViewModelFactory sFactory;

    private ViewModelFactory(){
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
        if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)){
            return (T) new RestaurantDetailViewModel(
                    RestaurantRepository.getInstance()
            );
        }
        if (modelClass.isAssignableFrom(ListRestaurantViewModel.class)){
            return (T) new ListRestaurantViewModel(new FireStoreService());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
