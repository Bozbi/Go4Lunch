package com.sbizzera.go4lunch.services;

import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

public class AuthHelper {

    private static AuthHelper sAuthHelper;
    private FirebaseAuth mFirebaseAuth;
    private AuthUI mAuthUI;


    private AuthHelper(FirebaseAuth firebaseAuth, AuthUI authUI) {
        mFirebaseAuth = firebaseAuth;
        mAuthUI = authUI;
    }

    public static AuthHelper getInstance(FirebaseAuth firebaseAuth, AuthUI authUI) {
        if (sAuthHelper == null) {
            sAuthHelper = new AuthHelper(firebaseAuth, authUI);
        }
        return sAuthHelper;
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
