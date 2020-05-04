package com.sbizzera.go4lunch.restaurant_activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.MainActivity;
import com.sbizzera.go4lunch.services.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RestaurantDtlActivity";

    private static final String INTENT_EXTRA_CODE = "INTENT_EXTRA_CODE";

    private RestaurantViewModel mModel;
    private List<RestaurantAdapterModel> mWorkmateList = new ArrayList<>();

    private RestaurantAdapter mAdapter;

    private ImageView restaurantImg;
    private FloatingActionButton fab;
    private TextView restaurantNameTxt;
    private ImageView star1Img;
    private ImageView star2Img;
    private ImageView star3Img;
    private TextView restaurantAddressTxt;
    private LinearLayout phoneBlockLiLay;
    private ImageView phoneImg;
    private TextView phoneTxt;
    private LinearLayout likeBlockLiLay;
    private ImageView likeImg;
    private TextView likeTxt;
    private LinearLayout websiteBlockLiLay;
    private ImageView webSiteImg;
    private TextView websiteTxt;
    private RecyclerView rcv;
    private ImageView backArrowImg;

    // TODO BOZBI NAVIGATE Comme le pattern newInstance() avec un fragment, tu peux utiliser le pattern navigate()
    //  pour contrôler les paramètres d'Intent de ton Activity
    public static Intent navigate(@NonNull Context context, @NonNull String restaurantId) {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra(INTENT_EXTRA_CODE, requireNonNull(restaurantId));

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantImg = findViewById(R.id.restaurant_photo_img);
        fab = findViewById(R.id.restaurant_check_fab);
        restaurantNameTxt = findViewById(R.id.restaurant_name_txt);
        star1Img = findViewById(R.id.restaurant_star_1_img);
        star2Img = findViewById(R.id.restaurant_star_2_img);
        star3Img = findViewById(R.id.restaurant_star_3_img);
        restaurantAddressTxt = findViewById(R.id.restaurant_address_txt);
        phoneBlockLiLay = findViewById(R.id.phone_block_lilay);
        phoneImg = findViewById(R.id.phone_img);
        phoneTxt = findViewById(R.id.phone_txt);
        likeBlockLiLay = findViewById(R.id.like_block_lilay);
        likeImg = findViewById(R.id.like_img);
        likeTxt = findViewById(R.id.like_txt);
        websiteBlockLiLay = findViewById(R.id.website_block_lilay);
        webSiteImg = findViewById(R.id.website_img);
        websiteTxt = findViewById(R.id.website_txt);
        backArrowImg = findViewById(R.id.back_fab);

        //Retrieve restaurant id in intent Extra
        String restaurantId = getIntent().getStringExtra(INTENT_EXTRA_CODE);

        mModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mModel.fetchRestaurantInfo(restaurantId);
        mModel.getModelLiveData().observe(this, this::updateUI);

        mAdapter = new RestaurantAdapter(mWorkmateList);
        RecyclerView rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(mAdapter);

        phoneBlockLiLay.setOnClickListener(this);
        likeBlockLiLay.setOnClickListener(this);
        websiteBlockLiLay.setOnClickListener(this);
        backArrowImg.setOnClickListener(this);
        fab.setOnClickListener(this);

    }


    private void updateUI(RestaurantActivityModel model) {
        Glide.with(restaurantImg).load(model.getPhotoUrl()).placeholder(R.drawable.restaurant_photo_placeholder).into(restaurantImg);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(model.getFabColor())));
        fab.setImageResource(model.getFabIcon());
        restaurantNameTxt.setText(model.getRestaurantName());
        star1Img.setVisibility(model.getStar1Visibility());
        star2Img.setVisibility(model.getStar2Visibility());
        star3Img.setVisibility(model.getStar3Visibility());
        restaurantAddressTxt.setText(model.getAddressText());
        phoneImg.getDrawable().setColorFilter(getResources().getColor(model.getPhoneColor()), PorterDuff.Mode.SRC_ATOP);
        phoneTxt.setTextColor(getResources().getColor(model.getPhoneColor()));
        phoneBlockLiLay.setClickable(model.getPhoneClickable());
        likeImg.setImageResource(model.getLikeIcon());
        webSiteImg.getDrawable().setColorFilter(getResources().getColor(model.getWebSiteColor()), PorterDuff.Mode.SRC_ATOP);
        websiteTxt.setTextColor(getResources().getColor(model.getWebSiteColor()));
        websiteBlockLiLay.setClickable(model.getWebSiteClickable());
        mWorkmateList = model.getWorkmatesList();
        mAdapter.setList(mWorkmateList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_fab: {
                finish();
                break;
            }
            case R.id.phone_block_lilay: {
                mModel.handlePhoneClick();
                break;
            }
            case R.id.website_block_lilay: {
                mModel.handleWebSiteClick();
                break;
            }
            case R.id.like_block_lilay: {
                mModel.handleLikeClick();
                break;
            }
            case R.id.restaurant_check_fab: {
                mModel.handleFabClick();
                break;
            }
        }
    }
}
