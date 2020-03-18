package com.sbizzera.go4lunch.view_models;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.App;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.RestaurantDetailModel;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse.DetailResult;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.FirebaseAuthService;
import com.sbizzera.go4lunch.services.RestaurantRepository;
import com.sbizzera.go4lunch.utils.Commons;

import java.util.ArrayList;
import java.util.List;


public class RestaurantDetailViewModel extends ViewModel {

    private static final String TAG = "RestaurantDetailVM";

    private String restaurantId;

    public RestaurantRepository mRestaurantRepository;
    private MediatorLiveData<RestaurantDetailModel> modelLiveData = new MediatorLiveData<>();

    RestaurantDetailViewModel(RestaurantRepository restaurantRepository) {
        mRestaurantRepository = restaurantRepository;
    }

    public LiveData<RestaurantDetailModel> getModelLiveData() {
        return modelLiveData;
    }

    public void fetchPlace(String id) {
        restaurantId = id;
        LiveData<DetailResult> placeLiveData = mRestaurantRepository.getRestaurantByID(id);
        modelLiveData.addSource(placeLiveData, place -> {
            modelLiveData.postValue(combineSources(place));
        });

    }

    private RestaurantDetailModel combineSources(DetailResult place) {
        String photoUrl = createPhotoUrlFromPhotoRef(place.getPhotosList());
        String restaurantName =place.getName();
        String restaurantAddress= createAdress(place.getAddressComponentList());
        String phoneNumber = place.getPhoneNumber();
        @ColorRes
        int phoneBlockColor = findPhoneBlockColor(place.getPhoneNumber());
        Boolean isPhoneClickable = findIsPhoneClickable(place.getPhoneNumber());
        String webSite = place.getWebSiteUrl();
        @ColorRes
        int webSiteBlockColor = findWebsiteBlockColor(place.getWebSiteUrl());
        Boolean isWebSiteClickable = findIsWebSiteClickable(place.getWebSiteUrl());




        return new RestaurantDetailModel(
                photoUrl,
                //TODO
                R.drawable.ic_check_light,
                restaurantName,
                restaurantAddress,
                //TODO
                R.drawable.ic_star_yellow,
                R.drawable.ic_star_yellow,
                R.drawable.ic_star_bordered,
                phoneNumber,
                phoneBlockColor,
                isPhoneClickable,
                //TODO
                R.drawable.ic_star_yellow,
                webSite,
                webSiteBlockColor,
                isWebSiteClickable,
                //TODO
                new ArrayList<>());
    }

    private Boolean findIsWebSiteClickable(String webSiteUrl) {
        if (webSiteUrl==null){
            return false;
        }
        return true;
    }

    private int findWebsiteBlockColor(String webSiteUrl) {
        if (webSiteUrl==null){
            return R.color.missingInfoColor;
        }
        return R.color.colorPrimary;
    }

    private Boolean findIsPhoneClickable(String phoneNumber) {
        if (phoneNumber==null){
            return false;
        }
        return true;
    }

    private int findPhoneBlockColor(String phoneNumber) {
        if (phoneNumber==null){
            return R.color.missingInfoColor;
        }
        return R.color.colorPrimary;
    }

    private String createAdress(List<DetailResult.AddressComponent> addressComponentList) {
        String streetNumber = addressComponentList.get(0).getValue();
        String streetName = addressComponentList.get(1).getValue().substring(0,1).toLowerCase()
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
        FireStoreService service = new FireStoreService();
        service.updateRestaurantLikeForUser(restaurantId);
    }

    public void handleFabClick() {
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
