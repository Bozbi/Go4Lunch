package com.sbizzera.go4lunch.services;

import android.content.Context;
import android.content.Intent;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.Arrays;
import java.util.List;

public class AuthService {

    private static AuthService sAuthService;
    private FirebaseAuth mFirebaseAuth;
    private AuthUI mAuthUI;


    private AuthService(FirebaseAuth firebaseAuth, AuthUI authUI) {
        mFirebaseAuth = firebaseAuth;
        mAuthUI = authUI;
    }

    public static AuthService getInstance(FirebaseAuth firebaseAuth, AuthUI authUI) {
        if (sAuthService == null) {
            sAuthService = new AuthService(firebaseAuth, authUI);
        }
        return sAuthService;
    }


    public Task<Void> logOut(Context context) {
        return mAuthUI.signOut(context);
    }

    public FirebaseUser getUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    public String getUserPhotoUrl() {
        String photoUrl = null;
        if (mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getPhotoUrl() != null) {
            photoUrl = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
        }
        return photoUrl;
    }

    public String getUserName() {
        String userName = null;
        if (mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getDisplayName() != null) {
            userName = mFirebaseAuth.getCurrentUser().getDisplayName();
        }
        return userName;
    }

    public String getUserFirstName() {
        String userName = null;
        if (mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getDisplayName() != null) {
            userName = mFirebaseAuth.getCurrentUser().getDisplayName();
            userName = Go4LunchUtils.getUserFirstName(userName);
        }
        return userName;
    }

    public String getUserEmail() {
        String userEmail = "";
        if (mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getEmail() != null) {
            userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        }
        return userEmail;
    }
}
