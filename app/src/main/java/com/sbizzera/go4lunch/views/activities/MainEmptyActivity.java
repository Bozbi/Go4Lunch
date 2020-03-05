package com.sbizzera.go4lunch.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.R;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class MainEmptyActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123;
    private List<AuthUI.IdpConfig> providers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("OnCreate");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            launchRestaurantAcitivity();
        } else {

            providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build()
            );

            AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(R.layout.login_layout)
                    .setGoogleButtonId(R.id.google_btn)
                    .setFacebookButtonId(R.id.facebook_btn)
                    .build();

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(providers)
                            .setTheme(R.style.LoginTheme)
                            .setAuthMethodPickerLayout(customLayout)
                            .build(),
                    RC_SIGN_IN);

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                launchRestaurantAcitivity();
                finish();
            } else {
                finish();
            }
        }

    }

    private void launchRestaurantAcitivity(){
        Intent intent = new Intent(this, ListRestaurantsActivity.class);
        startActivity(intent);
        finish();
    }
}

