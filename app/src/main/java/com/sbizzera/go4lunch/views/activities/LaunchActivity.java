package com.sbizzera.go4lunch.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sbizzera.go4lunch.services.FirebaseAuthService;

public class LaunchActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Checking if user is logged in.
        if (FirebaseAuthService.isUserLogged()) {
            launchRestaurantActivity();
        } else {
            //Launching FirebaseAuth activity
            startActivityForResult(FirebaseAuthService.getLoginIntent(), RC_SIGN_IN);
        }
    }


    //Results of FirebaseAuth activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                launchRestaurantActivity();
                finish();
            } else {
                finish();
            }
        }

    }

    //Launching ListRestaurantsActivity
    private void launchRestaurantActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

