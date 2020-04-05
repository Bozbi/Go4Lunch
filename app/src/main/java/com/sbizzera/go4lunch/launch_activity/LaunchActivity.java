package com.sbizzera.go4lunch.launch_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.MainActivity;
import com.sbizzera.go4lunch.services.FirebaseAuthService;

public class LaunchActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_empty);

        //Checking if user is logged in.
        if (FirebaseAuthService.isUserLogged()) {
            launchMainActivity();
            finish();
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
                launchMainActivity();
                finish();
            } else {
                finish();
            }
        }
    }

    //Launching ListRestaurantsActivity
    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

