package com.sbizzera.go4lunch.view_models;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sbizzera.go4lunch.DeviceLocator;
import com.sbizzera.go4lunch.PermissionHandler;
import com.sbizzera.go4lunch.services.RestaurantRepository;

public class MapFragmentViewModelFactory implements ViewModelProvider.Factory {
    private Activity mActivity;

    private static MapFragmentViewModelFactory sFactory;

    private MapFragmentViewModelFactory(Activity activity){
        mActivity = activity;
    }

    public static MapFragmentViewModelFactory getInstance(Activity activity){
        if(sFactory==null){
            synchronized (MapFragmentViewModelFactory.class){
                if (sFactory==null){
                    sFactory = new MapFragmentViewModelFactory(activity);
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
                    new DeviceLocator(mActivity),
                    PermissionHandler.getInstance(),
                    RestaurantRepository.getInstance()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
