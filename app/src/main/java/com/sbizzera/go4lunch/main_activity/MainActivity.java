package com.sbizzera.go4lunch.main_activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.sbizzera.go4lunch.events.RestaurantClickedListenable;
import com.sbizzera.go4lunch.list_fragment.ListFragment;
import com.sbizzera.go4lunch.main_activity.models.MainActivityModel;
import com.sbizzera.go4lunch.map_fragment.MapFragment;
import com.sbizzera.go4lunch.recipe_fragment.RecipeFragment;
import com.sbizzera.go4lunch.restaurant_activity.RestaurantActivity;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;
import com.sbizzera.go4lunch.utils.ViewModelFactory;
import com.sbizzera.go4lunch.workmates_fragment.WorkmatesFragment;
import com.sbizzera.go4lunch.your_lunch_dialog.YourLunchDialogFragment;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, OnItemBoundWithRestaurantClickListener {

    private static final int REQUEST_LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 234;

    private DrawerLayout drawerLayout;
    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserPhoto;
    private Switch notificationSwitch;
    private TextView switchText;
    private Menu mMenu;

    private MainActivityViewModel mViewModel;

    public static Intent navigate(DispatchActivity dispatchActivity) {
        return new Intent(dispatchActivity, MainActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declaration
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mUserName = navigationView.getHeaderView(0).findViewById(R.id.drawer_user_name);
        mUserEmail = navigationView.getHeaderView(0).findViewById(R.id.drawer_user_email);
        mUserPhoto = navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.drawer_settings);
        notificationSwitch = menuItem.getActionView().findViewById(R.id.notification_switch);
        switchText = menuItem.getActionView().findViewById(R.id.notification_switch_txt);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainActivityViewModel.class);

        wireUpNotificationSwitch();
        mViewModel.getModel().observe(this, this::updateUI);
        mViewModel.getViewActionSearch().observe(this, this::launchAutocomplete);
        mViewModel.getActionLE().observe(this, action -> {
            switch (action) {
                case ASK_LOCATION_PERMISSION: {
                    showPermissionAppropriateRequest();
                    break;
                }
                case SHOW_NOT_A_RESTAURANT_TOAST: {
                    Toast.makeText(this, R.string.not_a_restaurant, Toast.LENGTH_LONG).show();
                    break;
                }
                case LOG_OUT: {
                    startActivity(DispatchActivity.navigate(this));
                    finish();
                    break;
                }
            }
        });
        mViewModel.getViewActionYourLunch().observe(this, yourLunchModel -> {
            YourLunchDialogFragment dialog = YourLunchDialogFragment.newInstance(yourLunchModel);
            dialog.show(getSupportFragmentManager(), null);

        });
        mViewModel.getmViewActionLaunchRestaurantDetailsLE().observe(this, restaurantId -> {
            if (restaurantId != null) {
                launchRestaurantDetail(restaurantId);
            }
        });

        //Setting Custom ToolBar As ActionBar
        setSupportActionBar(toolbar);

        //Add Toggle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Set listener to bottom nav
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Session Log Out Back to mainEmpty
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(MapFragment.newInstance());
        }
    }

    private void updateUI(MainActivityModel model) {
        Glide.with(this).load(model.getUserPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(mUserPhoto);
        mUserName.setText(model.getUserName());
        mUserEmail.setText(model.getUserEmail());
        notificationSwitch.setChecked(!model.getNotificationOn());
        switchText.setText(model.getSwitchText());
    }

    private void wireUpNotificationSwitch() {
        notificationSwitch.setOnClickListener(v -> mViewModel.updateSharedPrefs(!((Switch) v).isChecked()));
    }

    // loadFragment to container
    private void loadFragment(Fragment fragmentToLoad) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToLoad).commit();
    }

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
        MenuItem searchBar = mMenu.findItem(R.id.search_bar);
        switch (menuItem.getItemId()) {
            case R.id.drawer_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                logOut();
                break;
            case R.id.bottom_nav_map_item:
                loadFragment(MapFragment.newInstance());
                searchBar.setVisible(true);
                break;
            case R.id.bottom_nav_list_item:
                loadFragment(ListFragment.newInstance());
                searchBar.setVisible(true);
                break;
            case R.id.bottom_nav_workmates_item:
                loadFragment(WorkmatesFragment.newInstance());
                searchBar.setVisible(false);
                break;
            case R.id.bottom_nav_receipe_item:
                loadFragment(RecipeFragment.Companion.newInstance());
                searchBar.setVisible(false);
                break;
            case R.id.drawer_your_lunch:
                mViewModel.yourLunchButtonClicked();
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
        if (item.getItemId() == R.id.search_bar) {
            mViewModel.showAutocomplete();
        }
        return true;
    }

    @Override
    public void onItemBoundWithRestaurantClick(String id) {
        launchRestaurantDetail(id);
    }

    private void launchRestaurantDetail(String id) {
        startActivity(RestaurantActivity.navigate(this, id));
    }

    private void showPermissionAppropriateRequest() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
            builder.setTitle(R.string.location_permission);
            builder.setMessage(R.string.permission_text);
            builder.setNegativeButton(R.string.back, (x, y) -> {
            });
            builder.setPositiveButton(R.string.go_to_permissions, (x, y) -> startActivity(Go4LunchUtils.getGoToPermissionIntent(this)));
            builder.show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void logOut() {
        mViewModel.logOutUser();
    }


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof RestaurantClickedListenable) {
            ((RestaurantClickedListenable) fragment).setListener(this);
        }
    }

    private void launchAutocomplete(RectangularBounds bounds) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.TYPES))
                .setHint(getString(R.string.autocomplete_hint))
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

                    mViewModel.onAutocompleteClick(
                            Autocomplete.getPlaceFromIntent(data).getTypes(),
                            Autocomplete.getPlaceFromIntent(data).getId()
                    );
                }
            }
        }
    }

}
