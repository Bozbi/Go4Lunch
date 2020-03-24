package com.sbizzera.go4lunch.views.activities;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.RestaurantDetailAdapterModel;
import com.sbizzera.go4lunch.model.RestaurantActivityDetailModel;
import com.sbizzera.go4lunch.view_models.RestaurantDetailViewModel;
import com.sbizzera.go4lunch.view_models.ViewModelFactory;
import com.sbizzera.go4lunch.views.adapters.RestaurantDetailWorkmateAdapter;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RestaurantDtlActivity";

    private RestaurantDetailViewModel mModel;
    private List<RestaurantDetailAdapterModel> mWorkmateList = new ArrayList<>();

    private RestaurantDetailWorkmateAdapter mAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

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
        likeImg.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        likeTxt = findViewById(R.id.like_txt);
        websiteBlockLiLay = findViewById(R.id.website_block_lilay);
        webSiteImg = findViewById(R.id.website_img);
        websiteTxt = findViewById(R.id.website_txt);
        backArrowImg = findViewById(R.id.back_arrow_img);

        //Retrieve restaurant id in intent Extra
        String restaurantId = getIntent().getStringExtra(ListRestaurantsActivity.INTENT_EXTRA_CODE);

        mModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);
        mModel.fetchRestaurantInfo(restaurantId);
        mModel.getModelLiveData().observe(this, this::updateUI);

        mAdapter = new RestaurantDetailWorkmateAdapter(mWorkmateList);
        RecyclerView rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(mAdapter);

        phoneBlockLiLay.setOnClickListener(this);
        likeBlockLiLay.setOnClickListener(this);
        websiteBlockLiLay.setOnClickListener(this);
        backArrowImg.setOnClickListener(this);
        fab.setOnClickListener(this);

    }


    private void updateUI(RestaurantActivityDetailModel model) {
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
            case R.id.back_arrow_img: {
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
//            case R.id.like_block_lilay: {
//                mModel.handleLikeClick();
//                break;
//            }
//            case R.id.restaurant_check_fab: {
//                mModel.handleFabClick();
//                break;
//            }
        }
    }
}
