package com.sbizzera.go4lunch.dispatch_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.MainActivity;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;


public class DispatchActivity extends AppCompatActivity {

    private static final int AUTH_UI_INSTANCE_REQ_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            launchAuthActivity();
        } else {
            launchMainActivity();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Timber.d("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_UI_INSTANCE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                launchMainActivity();
                finish();
            }
        } else {
            launchAuthActivity();
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchAuthActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(R.layout.login_layout)
                .setGoogleButtonId(R.id.google_btn)
                .setFacebookButtonId(R.id.facebook_btn)
                .build();

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setAuthMethodPickerLayout(customLayout)
                .build();

        startActivityForResult(intent, AUTH_UI_INSTANCE_REQ_CODE);
    }
}
