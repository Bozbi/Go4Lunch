package com.sbizzera.go4lunch.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// TODO BOZBI Unused
public class FirebaseUserRepo {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> mHasUserLoggedIn = new MutableLiveData<>();

    public FirebaseUserRepo(){
        checkHasUserLoggedIn();
    }

    private void checkHasUserLoggedIn() {
        if (mFirebaseAuth.getCurrentUser()!=null){
            mHasUserLoggedIn.setValue(true);
        }
        else mHasUserLoggedIn.setValue(false);
    }

    public LiveData<Boolean> hasUserLoggedInLD (){
        return mHasUserLoggedIn;
    }
}
