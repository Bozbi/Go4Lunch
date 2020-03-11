package com.sbizzera.go4lunch.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.views.fragments.ListFragment;
import com.sbizzera.go4lunch.views.fragments.MapFragment;
import com.sbizzera.go4lunch.views.fragments.WorkmatesFragment;

public class ListRestaurantsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;


    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);

        //Declaration
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.drawer_user_name);
        mUserEmail = navigationView.getHeaderView(0).findViewById(R.id.drawer_user_email);
        mUserPhoto = navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        //Setting Custom ToolBar As ActionBar
        setSupportActionBar(toolbar);

        //Add Toogle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Set listener to bottom nav
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Session Log Out Back to mainEmpty
        navigationView.setNavigationItemSelectedListener(this);
        getAnddisplayUserInfo();

        loadFragment(new MapFragment());
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        FRAGMENTS
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // loadFragment to container
    private void loadFragment(Fragment fragmentToLoad) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToLoad).commit();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        UI
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //If Drawer is open, we want to close it on backbutton pressed not exiting app
    @Override
    public void onBackPressed() {
        //TODO add a byebye message
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Check wich item of Drawer Menu or BottomNav has been selected and triggers response
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                logOut();
                break;
            case R.id.bottom_nav_map_item:
                loadFragment(new MapFragment());
                break;
            case R.id.bottom_nav_list_item:
                loadFragment(new ListFragment());
                break;
            case R.id.bottom_nav_workmates_item:
                loadFragment(new WorkmatesFragment());
                break;
        }
        return true;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        Auth
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //LOG OUT User and back to go back to main then login
    private void logOut() {
        FirebaseAuthService.logOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ListRestaurantsActivity.this, MainEmptyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    //Get Values from Firebase user Instance and push them in views
    private void getAnddisplayUserInfo() {
        FirebaseUser user = FirebaseAuthService.getUser();
        mUserName.setText(user.getDisplayName());
        mUserEmail.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(mUserPhoto);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
