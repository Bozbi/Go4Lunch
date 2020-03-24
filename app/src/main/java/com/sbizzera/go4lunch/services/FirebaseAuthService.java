package com.sbizzera.go4lunch.services;

import android.content.Context;
import android.content.Intent;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.R;

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
}
