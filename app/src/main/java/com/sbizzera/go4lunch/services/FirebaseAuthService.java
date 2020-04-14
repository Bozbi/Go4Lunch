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

public class FirebaseAuthService {

    private static FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private static AuthUI mAuthUi = AuthUI.getInstance();

    public static boolean isUserLogged() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    public static Intent getLoginIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(R.layout.login_layout)
                .setGoogleButtonId(R.id.google_btn)
                .setFacebookButtonId(R.id.facebook_btn)
                .build();

        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setAuthMethodPickerLayout(customLayout)
                .build();

    }

    public static Task<Void> logOut(Context context) {
        return mAuthUi.signOut(context);
    }

    public static FirebaseUser getUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    public static String getUserPhotoUrl() {
        String photoUrl = "";
        if (mFirebaseAuth.getCurrentUser() != null&& mFirebaseAuth.getCurrentUser().getPhotoUrl()!=null) {
            photoUrl =  mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
        }
        return photoUrl;
    }
    public static String getUserName(){
        String userName = "";
        if (mFirebaseAuth.getCurrentUser()!=null&&mFirebaseAuth.getCurrentUser().getDisplayName()!=null){
            userName = mFirebaseAuth.getCurrentUser().getDisplayName();
        }
        return userName;
    }

    public static String getUserFirstName(){
        String userName = "";
        if (mFirebaseAuth.getCurrentUser()!=null&&mFirebaseAuth.getCurrentUser().getDisplayName()!=null){
            userName = mFirebaseAuth.getCurrentUser().getDisplayName();
            userName = Go4LunchUtils.getUserFirstName(userName);
        }
        return userName;
    }

    public static String getUserEmail(){
        String userEmail = "";
        if (mFirebaseAuth.getCurrentUser()!=null&&mFirebaseAuth.getCurrentUser().getEmail()!=null){
            userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        }
        return userEmail;
    }
}
