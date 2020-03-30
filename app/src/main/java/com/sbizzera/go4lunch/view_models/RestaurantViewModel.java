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
import com.sbizzera.go4lunch.model.RestaurantActivityModel;
import com.sbizzera.go4lunch.model.RestaurantAdapterModel;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreUser;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse.DetailResult;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.utils.Commons;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.ArrayList;
import java.util.List;


public class RestaurantViewModel extends ViewModel {

    private static final String TAG = "RestaurantDetailVM";


    private GooglePlacesService mGooglePlacesService;
    private FireStoreService mFirestoreService;
    private MediatorLiveData<RestaurantActivityModel> modelLiveData = new MediatorLiveData<>();
    private LiveData<DetailResult> placeDetailLiveData;

    RestaurantViewModel(GooglePlacesService googlePlacesService, FireStoreService firestore) {
        mGooglePlacesService = googlePlacesService;
        mFirestoreService = firestore;
    }

    public LiveData<RestaurantActivityModel> getModelLiveData() {
        return modelLiveData;
    }

    public void fetchRestaurantInfo(String id) {
//        placeDetailLiveData = mGooglePlacesService.getRestaurantDetailsById(id);
        LiveData<Boolean> isRestaurantLikedByUserLiveData = mFirestoreService.isRestaurantLikedByUser(id);
        LiveData<Integer> restaurantLikeCountLiveData = mFirestoreService.getRestaurantLikesCount(id);
        LiveData<Boolean> isRestaurantTodayUserChoiceLiveData = mFirestoreService.isRestaurantChosenByUserToday(id);
        LiveData<List<FireStoreUser>> todayListOfUsersLiveData = mFirestoreService.getTodayListOfUsers(id);

        modelLiveData.addSource(placeDetailLiveData, place -> {
            modelLiveData.postValue(combineSources(place, isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoiceLiveData.getValue(), todayListOfUsersLiveData.getValue()));
        });

        modelLiveData.addSource(isRestaurantLikedByUserLiveData, isRestaurantLikedByUser -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUser, restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoiceLiveData.getValue(), todayListOfUsersLiveData.getValue()));
        });

        modelLiveData.addSource(restaurantLikeCountLiveData, restaurantLikeCount -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCount, isRestaurantTodayUserChoiceLiveData.getValue(), todayListOfUsersLiveData.getValue()));
        });

        modelLiveData.addSource(isRestaurantTodayUserChoiceLiveData, isRestaurantTodayUserChoice -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoice, todayListOfUsersLiveData.getValue()));
        });

        modelLiveData.addSource(todayListOfUsersLiveData, todayListOfUsers -> {
            modelLiveData.postValue(combineSources(placeDetailLiveData.getValue(), isRestaurantLikedByUserLiveData.getValue(), restaurantLikeCountLiveData.getValue(), isRestaurantTodayUserChoiceLiveData.getValue(), todayListOfUsers));
        });
    }

    private RestaurantActivityModel combineSources(DetailResult place, Boolean isRestaurantLikedByUser, Integer restaurantLikeCount, Boolean isRestaurantTodayUserChoice, List<FireStoreUser> todayListOfUsers) {

        String photoUrl = getPhotoUrlFromPhotoRef(place);
        String restaurantName = getName(place);
        String restaurantAddress = getAddress(place);
        String phoneNumber = getPhoneNumber(place);
        String webSite = getWebSite(place);
        @ColorRes
        int phoneBlockColor = getPhoneBlockColor(phoneNumber);
        Boolean isPhoneClickable = isPhoneClickable(phoneNumber);
        @ColorRes
        int webSiteBlockColor = getWebsiteBlockColor(webSite);
        Boolean isWebSiteClickable = isWebSiteClickable(webSite);
        @DrawableRes
        int likeIconRes = getLikeIcon(isRestaurantLikedByUser);
        @IntegerRes
        int star1Visibility = setStar1Visibility(restaurantLikeCount);
        @IntegerRes
        int star2Visibility = setStar2Visibility(restaurantLikeCount);
        @IntegerRes
        int star3Visibility = setStar3Visibility(restaurantLikeCount);
        @IntegerRes
        int fabIcon = getFabIcon(isRestaurantTodayUserChoice);
        @ColorRes
        int fabColor = getFabColor(isRestaurantTodayUserChoice);
        List<RestaurantAdapterModel> todaysLunchers = getLunchers(todayListOfUsers);


        return new RestaurantActivityModel(
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
                todaysLunchers);
    }

    private List<RestaurantAdapterModel> getLunchers(List<FireStoreUser> todayListOfUsers) {
        List<RestaurantAdapterModel> listToReturn = new ArrayList<>();
        if (todayListOfUsers != null) {
            for (FireStoreUser user : todayListOfUsers) {
                String text = Go4LunchUtils.getUserFirstName(user.getUserName()) + " is eating here";
                RestaurantAdapterModel userModel = new RestaurantAdapterModel(
                        user.getUserAvatarUrl(),
                        text
                );
                listToReturn.add(userModel);
            }
        }
        return listToReturn;
    }


    public void handleLikeClick() {
        //check that a restaurant has been fetch
        if (placeDetailLiveData.getValue() != null) {
            mFirestoreService.updateRestaurantLike(placeDetailLiveData.getValue());
        }
    }

    public void handleFabClick() {
        //check that a restaurant has been fetch
        if (placeDetailLiveData.getValue() != null) {
            mFirestoreService.updateRestaurantChoice(placeDetailLiveData.getValue());
        }
    }

    private String getWebSite(DetailResult place) {
        //Check nulls
        if (place == null || place.getWebSiteUrl() == null) {
            return null;
        }

        //if not return webSite
        return place.getWebSiteUrl();
    }

    private String getPhoneNumber(DetailResult place) {
        //Check nulls
        if (place == null || place.getPhoneNumber() == null) {
            return null;
        }

        //if not return phone number
        return place.getPhoneNumber();

    }

    private String getName(DetailResult place) {
        //Check nulls
        if (place == null || place.getName() == null) {
            return null;
        }
        //if not return name
        return place.getName();

    }

    private int getFabColor(Boolean isRestaurantTodayUserChoice) {
        //check nulls
        if (isRestaurantTodayUserChoice == null || !isRestaurantTodayUserChoice) {
            return R.color.white;
        }
        return R.color.green;
    }

    private int getFabIcon(Boolean isRestaurantTodayUserChoice) {
        //check nulls
        if (isRestaurantTodayUserChoice == null || !isRestaurantTodayUserChoice) {
            return R.drawable.restaurant_icon_grey;
        }
        return R.drawable.restaurant_icon_white;
    }

    private int setStar1Visibility(Integer restaurantLikeCount) {
        //check nulls
        if (restaurantLikeCount == null || restaurantLikeCount < 1) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private int setStar2Visibility(Integer restaurantLikeCount) {
        //check nulls
        if (restaurantLikeCount == null || restaurantLikeCount < 2) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private int setStar3Visibility(Integer restaurantLikeCount) {
        //check nulls
        if (restaurantLikeCount == null || restaurantLikeCount < 3) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private int getLikeIcon(Boolean isRestaurantLikedByUser) {
        //check for nulls or false
        if (isRestaurantLikedByUser == null || !isRestaurantLikedByUser) {
            //if return bordered star
            return R.drawable.ic_star_bordered;
        }
        // if not return plain star
        return R.drawable.ic_star_yellow;
    }

    private Boolean isWebSiteClickable(String webSiteUrl) {
        //check nulls
        if (webSiteUrl == null) {
            return false;
        }
        //if not return isClickable
        return true;
    }

    private int getWebsiteBlockColor(String webSiteUrl) {
        //check nulls
        if (webSiteUrl == null) {
            return R.color.missingInfoColor;
        }
        // if not return color
        return R.color.colorPrimary;
    }

    private Boolean isPhoneClickable(String phoneNumber) {
        //check nulls
        if (phoneNumber == null) {
            return false;
        }
        //if not return isClickable
        return true;
    }

    private int getPhoneBlockColor(String phoneNumber) {
        //check nulls
        if (phoneNumber == null) {
            return R.color.missingInfoColor;
        }

        //if not return color
        return R.color.colorPrimary;
    }

    private String getAddress(DetailResult place) {
        //check null
        if (place == null || place.getAddressComponentList() == null) {
            return null;
        }
        //if not build address
        List<DetailResult.AddressComponent> componentList = place.getAddressComponentList();
        String streetNumber = componentList.get(0).getValue();
        String streetName = componentList.get(1).getValue().substring(0, 1).toLowerCase() + componentList.get(1).getValue().substring(1);
        return streetNumber + " " + streetName;
    }


    private String getPhotoUrlFromPhotoRef(DetailResult place) {
        //check if either restaurant or photolist or firstPhoto not null
        if (place == null || place.getPhotosList() == null || place.getPhotosList().get(0) == null) {
            return null;
        }

        //if not return the URL
        String photoRef = place.getPhotosList().get(0).getPhotoReference();
        return new Uri.Builder().scheme("https")
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
