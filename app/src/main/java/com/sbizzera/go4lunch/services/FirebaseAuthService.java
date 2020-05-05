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

// TODO BOZBI Pas de static, fait en un Singleton plutôt... Plus facile à tester :)
public class FirebaseAuthService {

    private static FirebaseAuth sFirebaseAuth = FirebaseAuth.getInstance();
    private static AuthUI mAuthUi = AuthUI.getInstance();



    // TODO BOZBI A nettoyer, c'est mieux dans ta dispatcher activity :p
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
        return sFirebaseAuth.getCurrentUser();
    }

    public static String getUserPhotoUrl() {
        String photoUrl = "";
        if (sFirebaseAuth.getCurrentUser() != null&& sFirebaseAuth.getCurrentUser().getPhotoUrl()!=null) {
            photoUrl =  sFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
        }
        return photoUrl;
    }
    public static String getUserName(){
        String userName = "";
        if (sFirebaseAuth.getCurrentUser()!=null&& sFirebaseAuth.getCurrentUser().getDisplayName()!=null){
            userName = sFirebaseAuth.getCurrentUser().getDisplayName();
        }
        return userName;
    }

    public static String getUserFirstName(){
        String userName = "";
        if (sFirebaseAuth.getCurrentUser()!=null&& sFirebaseAuth.getCurrentUser().getDisplayName()!=null){
            userName = sFirebaseAuth.getCurrentUser().getDisplayName();
            userName = Go4LunchUtils.getUserFirstName(userName);
        }
        return userName;
    }

    public static String getUserEmail(){
        String userEmail = "";
        if (sFirebaseAuth.getCurrentUser()!=null&& sFirebaseAuth.getCurrentUser().getEmail()!=null){
            userEmail = sFirebaseAuth.getCurrentUser().getEmail();
        }
        return userEmail;
    }
}
