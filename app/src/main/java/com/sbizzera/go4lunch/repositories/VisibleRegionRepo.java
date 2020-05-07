package com.sbizzera.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.VisibleRegion;

public class VisibleRegionRepo {
    private static VisibleRegionRepo sVisibleRegionRepo;

    private MutableLiveData<VisibleRegion> mLastMapVisibleRegion = new MutableLiveData<>();
    private MutableLiveData<VisibleRegion> mLastNearbyRestaurantsFetchVisibleRegion = new MutableLiveData<>();


    public static VisibleRegionRepo getInstance() {
        if (sVisibleRegionRepo == null) {
            sVisibleRegionRepo = new VisibleRegionRepo();
        }
        return sVisibleRegionRepo;
    }

    public LiveData<VisibleRegion> getLastMapVisibleRegion() {
        return mLastMapVisibleRegion;
    }

    public void setLastMapVisibleRegion(VisibleRegion lastMapVisibleRegion) {
        mLastMapVisibleRegion.setValue(lastMapVisibleRegion);
    }

    public LiveData<VisibleRegion> getLastNearbyRestaurantsFetchVisibleRegion() {
        return mLastNearbyRestaurantsFetchVisibleRegion;
    }

    public void setLastNearbyRestaurantsFetchVisibleRegion(VisibleRegion lastNearbyRestaurantsFetchVisibleRegion) {
        mLastNearbyRestaurantsFetchVisibleRegion.setValue(lastNearbyRestaurantsFetchVisibleRegion);
    }


}
