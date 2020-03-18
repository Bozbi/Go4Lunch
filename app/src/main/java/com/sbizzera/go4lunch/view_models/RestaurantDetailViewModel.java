package com.sbizzera.go4lunch.view_models;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.RestaurantDetailModel;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse.DetailResult;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.RestaurantRepository;
import com.sbizzera.go4lunch.utils.Commons;

import java.util.ArrayList;
import java.util.List;


public class RestaurantDetailViewModel extends ViewModel {

    private static final String TAG = "RestaurantDetailVM";


    private RestaurantRepository mRestaurantRepository;
    private FireStoreService mFirestore;
    private MediatorLiveData<RestaurantDetailModel> modelLiveData = new MediatorLiveData<>();
    private LiveData<DetailResult> placeDetailLiveData;

    RestaurantDetailViewModel(RestaurantRepository restaurantRepository, FireStoreService firestore) {
        mRestaurantRepository = restaurantRepository;
        mFirestore = firestore;

    }

    public LiveData<RestaurantDetailModel> getModelLiveData() {
        return modelLiveData;
    }

    public void fetchPlace(String id) {
        mFirestore.setLikeAndChoiceListener(id);

        placeDetailLiveData = mRestaurantRepository.getRestaurantByID(id);
        LiveData<Boolean> isRestaurantLikedByUserLiveData = mFirestore.getIsRestaurantLikedByUserLiveData();
        LiveData<Integer> restaurantLikeCountLiveData = mFirestore.getRestaurantLikeCount();
        LiveData<Boolean> isRestaurantTodayUserChoiceLiveData = mFirestore.getRestaurantTodayUserChoice();

        modelLiveData.addSource(placeDetailLiveData, place -> {
            modelLiveData.postValue(combineSources(place, isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoiceLiveData.getValue()));
        });

        modelLiveData.addSource(isRestaurantLikedByUserLiveData, isRestaurantLikedByUser -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUser, restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoiceLiveData.getValue()));
        });

        modelLiveData.addSource(restaurantLikeCountLiveData, restaurantLikeCount -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCount, isRestaurantTodayUserChoiceLiveData.getValue()));
        });

        modelLiveData.addSource(isRestaurantTodayUserChoiceLiveData, isRestaurantTodayUserChoice -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoice));
        });
    }

    private RestaurantDetailModel combineSources(DetailResult place, Boolean isRestaurantLikedByUser, Integer restaurantLikeCount, Boolean isRestaurantTodayUserChoice) {
        //TODO Comment virer cette ligne
        if (place == null || isRestaurantLikedByUser == null || restaurantLikeCount == null || isRestaurantTodayUserChoice == null) {
            return new RestaurantDetailModel(null, R.drawable.restaurant_icon_grey, R.color.white, null, null, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, null, R.color.missingInfoColor, false, R.drawable.ic_star_yellow, null, R.color.missingInfoColor, false, new ArrayList<>());
        }
        String photoUrl = createPhotoUrlFromPhotoRef(place.getPhotosList());
        String restaurantName = place.getName();
        String restaurantAddress = createAdress(place.getAddressComponentList());
        String phoneNumber = place.getPhoneNumber();
        @ColorRes
        int phoneBlockColor = findPhoneBlockColor(place.getPhoneNumber());
        Boolean isPhoneClickable = findIsPhoneClickable(place.getPhoneNumber());
        String webSite = place.getWebSiteUrl();
        @ColorRes
        int webSiteBlockColor = findWebsiteBlockColor(place.getWebSiteUrl());
        Boolean isWebSiteClickable = findIsWebSiteClickable(place.getWebSiteUrl());
        @DrawableRes
        int likeIconRes = findLikeIcon(isRestaurantLikedByUser);
        @IntegerRes
        int star1Visibility = setStar1Visibility(restaurantLikeCount);
        @IntegerRes
        int star2Visibility = setStar2Visibility(restaurantLikeCount);
        @IntegerRes
        int star3Visibility = setStar3Visibility(restaurantLikeCount);
        @IntegerRes
        int fabIcon = findFabIcon(isRestaurantTodayUserChoice);
        @ColorRes
        int fabColor = findFabColor(isRestaurantTodayUserChoice);


        return new RestaurantDetailModel(
                photoUrl,
                fabIcon,
                fabColor,
                restaurantName,
                restaurantAddress,
                star1Visibility,
                star2Visibility,
                star3Visibility,
                phoneNumber,
                phoneBlockColor,
                isPhoneClickable,
                likeIconRes,
                webSite,
                webSiteBlockColor,
                isWebSiteClickable,
                //TODO
                new ArrayList<>());
    }

    private int findFabColor(Boolean isRestaurantTodayUserChoice) {
        if (isRestaurantTodayUserChoice){
            return R.color.green;
        }
        return R.color.white;
    }

    private int findFabIcon(Boolean isRestaurantTodayUserChoice) {
        if(isRestaurantTodayUserChoice){
            return R.drawable.restaurant_icon_white;
        }
        return R.drawable.restaurant_icon_grey;
    }

    private int setStar1Visibility(Integer restaurantLikeCount) {
        if (restaurantLikeCount < 1) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private int setStar2Visibility(Integer restaurantLikeCount) {
        if (restaurantLikeCount < 2) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private int setStar3Visibility(Integer restaurantLikeCount) {
        if (restaurantLikeCount < 3) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private int findLikeIcon(Boolean isRestaurantLikedByUser) {
        if (isRestaurantLikedByUser) {
            return R.drawable.ic_star_yellow;
        } else {
            return R.drawable.ic_star_bordered;
        }
    }

    private Boolean findIsWebSiteClickable(String webSiteUrl) {
        if (webSiteUrl == null) {
            return false;
        }
        return true;
    }

    private int findWebsiteBlockColor(String webSiteUrl) {
        if (webSiteUrl == null) {
            return R.color.missingInfoColor;
        }
        return R.color.colorPrimary;
    }

    private Boolean findIsPhoneClickable(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        return true;
    }

    private int findPhoneBlockColor(String phoneNumber) {
        if (phoneNumber == null) {
            return R.color.missingInfoColor;
        }
        return R.color.colorPrimary;
    }

    private String createAdress(List<DetailResult.AddressComponent> addressComponentList) {
        String streetNumber = addressComponentList.get(0).getValue();
        String streetName = addressComponentList.get(1).getValue().substring(0, 1).toLowerCase()
                + addressComponentList.get(1).getValue().substring(1);
        StringBuilder builder = new StringBuilder();
        return builder.append(streetNumber).append(" ").append(streetName).toString();
    }


    private String createPhotoUrlFromPhotoRef(List<DetailResult.Photos> photosList) {
        String photoUrl = null;
        if (photosList != null
        ) {
            if (photosList.get(0).getPhotoReference() != null) {
                String photoRef = photosList.get(0).getPhotoReference();
                Uri.Builder builder = new Uri.Builder();
                photoUrl = builder.scheme("https")
                        .authority("maps.googleapis.com")
                        .appendPath("maps")
                        .appendPath("api")
                        .appendPath("place")
                        .appendPath("photo")
                        .appendQueryParameter("maxwidth", "600")
                        .appendQueryParameter("photoreference", photoRef)
                        .appendQueryParameter("key", Commons.PLACES_API_KEY)
                        .toString();
            }
        }
        return photoUrl;
    }


    public void handleLikeClick() {
        mFirestore.updateRestaurantLike(placeDetailLiveData.getValue());
    }

    public void handleFabClick() {
        mFirestore.updateRestaurantChoice(placeDetailLiveData.getValue());
    }


    public void handleWebSiteClick() {
        Uri webpage = Uri.parse(modelLiveData.getValue().getWebSiteUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(App.getApplication().getPackageManager()) != null) {
            App.getApplication().startActivity(intent);
        }
    }

    public void handlePhoneClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + modelLiveData.getValue().getPhoneNumber()));
        if (intent.resolveActivity(App.getApplication().getPackageManager()) != null) {
            App.getApplication().startActivity(intent);
        }
    }
}
