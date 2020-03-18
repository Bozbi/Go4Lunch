package com.sbizzera.go4lunch.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;

public class MainEmptyActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 012;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuthService.isUserLoged()) {
            launchRestaurantAcitivity();
        } else {
            startActivityForResult(FirebaseAuthService.getLoginIntent(), RC_SIGN_IN);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                launchRestaurantAcitivity();
                finish();
            } else {
                finish();
            }
        }

    }

    private void launchRestaurantAcitivity() {
        Intent intent = new Intent(this, ListRestaurantsActivity.class);
        startActivity(intent);
        finish();
    }
}

