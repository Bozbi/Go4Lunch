package com.sbizzera.go4lunch.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.dispatch_activity.DispatchActivity;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.main_activity.fragments.list_fragment.ListFragment;
import com.sbizzera.go4lunch.main_activity.fragments.map_fragment.MapFragment;
import com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment.WorkmatesFragment;
import com.sbizzera.go4lunch.main_activity.your_lunch_dialog.YourLunchDialogFragment;
import com.sbizzera.go4lunch.restaurant_activity.RestaurantActivity;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.ViewModelFactory;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, OnItemBoundWithRestaurantClickListener {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 234;
    public static final String INTENT_EXTRA_CODE = "INTENT_EXTRA_CODE";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 123;
    private DrawerLayout drawerLayout;

    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserPhoto;
    private Toolbar mToolbar;
    private Switch notificationSwitch;
    private TextView switchText;
    private Menu mMenu;
    MainActivityViewModel mViewModel;


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
        mToolbar = findViewById(R.id.toolbar);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.drawer_settings);
        notificationSwitch = menuItem.getActionView().findViewById(R.id.notification_switch);
        switchText = menuItem.getActionView().findViewById(R.id.notification_switch_txt);
        wireUpNotificationSwitch();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainActivityViewModel.class);
        mViewModel.getModel().observe(this, this::updateUI);
        mViewModel.getActionLE().observe(this, action -> {
            switch (action) {
                case SHOW_AUTOCOMPLETE: {
                    if (mViewModel.getMapCurrentRectangularBounds() != null) {
                        launchAutocomplete(mViewModel.getMapCurrentRectangularBounds());
                    }
                    break;
                }
                case SHOW_RESTAURANT_DETAILS: {
                    if (mViewModel.getCurrentAutocompleteRestaurantID() != null) {
                        launchRestaurantDetail(mViewModel.getCurrentAutocompleteRestaurantID());
                    }
                    break;
                }
                case SHOW_NOT_A_RESTAURANT_TOAST: {
                    Toast.makeText(this, "This is not a restaurant", Toast.LENGTH_LONG).show();
                    break;
                }
            }

        });

        //Setting Custom ToolBar As ActionBar
        setSupportActionBar(mToolbar);

        //Add Toggle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Set listener to bottom nav
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Session Log Out Back to mainEmpty
        navigationView.setNavigationItemSelectedListener(this);

        loadFragment(MapFragment.newInstance());


    }


    private void updateUI(MainActivityModel model) {
        Glide.with(this).load(model.getUserPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(mUserPhoto);
        mUserName.setText(model.getUserName());
        mUserEmail.setText(model.getUserEmail());
        mToolbar.setTitle(model.getToolBarTitle());
        notificationSwitch.setChecked(!model.getNotificationOn());
        switchText.setText(model.getSwitchText());
    }

    private void wireUpNotificationSwitch() {
        notificationSwitch.setOnClickListener(v -> mViewModel.updateSharedPrefs(!((Switch) v).isChecked()));
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

    //If Drawer is open, we want to close it on backButton pressed not exiting app
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Check which item of Drawer Menu or BottomNav has been selected and triggers response
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                logOut();
                break;
            case R.id.bottom_nav_map_item:
                loadFragment(MapFragment.newInstance());
                mMenu.getItem(0).setVisible(true);

                break;
            case R.id.bottom_nav_list_item:
                loadFragment(ListFragment.newInstance());
                mMenu.getItem(0).setVisible(true);

                break;
            case R.id.bottom_nav_workmates_item:
                loadFragment(WorkmatesFragment.newInstance());
                mMenu.getItem(0).setVisible(false);

                break;
            case R.id.drawer_your_lunch:
                YourLunchDialogFragment dialog = YourLunchDialogFragment.newInstance();
                dialog.show(getSupportFragmentManager(), "TAG");
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_bar:
                mViewModel.showAutocomplete();
                break;

        }
        return true;
    }

    @Override
    public void onItemBoundWithRestaurantClick(String id) {
        launchRestaurantDetail(id);
    }

    private void launchRestaurantDetail(String id) {
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra(INTENT_EXTRA_CODE, id);
        startActivity(intent);
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
                        Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof MapFragment) {
            ((MapFragment) fragment).setListener(this);

        }
        if (fragment instanceof ListFragment) {
            ((ListFragment) fragment).setListener(this);

        }
        if (fragment instanceof WorkmatesFragment) {
            ((WorkmatesFragment) fragment).setListener(this);

        }
        if (fragment instanceof YourLunchDialogFragment) {
            ((YourLunchDialogFragment) fragment).setListener(this);
        }
    }

    private void launchAutocomplete(RectangularBounds bounds) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.TYPES))
                .setHint("find restaurants in area")
                .setCountry("FR")
                .setLocationBias(bounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mViewModel.onAutocompleteClick(data);
                }
            }
        }
    }
}
