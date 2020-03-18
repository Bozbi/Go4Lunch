package com.sbizzera.go4lunch.view_models;

import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.services.FireStoreService;

public class ListRestaurantViewModel extends ViewModel {

    private FireStoreService fireStore;

    public ListRestaurantViewModel(FireStoreService fireStore) {
        this.fireStore = fireStore;
    }

    public void checkUserAndInsertInDB() {
        fireStore.handleUserInFireStore();
    }
}
