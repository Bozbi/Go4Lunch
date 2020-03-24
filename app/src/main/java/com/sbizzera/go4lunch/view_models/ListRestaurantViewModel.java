package com.sbizzera.go4lunch.view_models;

import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.services.FireStoreService;

public class ListRestaurantViewModel extends ViewModel {

    private FireStoreService fireStore;

    ListRestaurantViewModel(FireStoreService fireStore) {
        this.fireStore = fireStore;
    }

    public void updateUserInDb() {
        fireStore.updateUserInDb();
    }
}
