package com.sbizzera.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ListRestaurantsActivity extends AppCompatActivity {

    private Button logOut;
    private TextView userName;
    private TextView userID;
    private TextView userEmail;
    private ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);


        logOut = findViewById(R.id.log_out);
        userID = findViewById(R.id.userID);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhoto = findViewById(R.id.userphoto);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(ListRestaurantsActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmailst = user.getEmail();
        String userNamest = user.getDisplayName();
        String userIdst = user.getUid();
        String userPhotoUrl = user.getPhotoUrl().toString();


        userName.setText(userNamest);
        userEmail.setText(userEmailst);
        userID.setText(userIdst);

        Glide.with(this).load(userPhotoUrl).apply(RequestOptions.circleCropTransform()).into(userPhoto);

        Log.e("TAG","Name : "+userNamest+", Email : "+userEmailst+", PhotoUrl : "+userPhotoUrl +", id : "+userIdst);
    }
}
