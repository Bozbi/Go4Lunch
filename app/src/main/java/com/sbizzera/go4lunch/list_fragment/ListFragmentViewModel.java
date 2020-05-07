package com.sbizzera.go4lunch.list_fragment;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.BuildConfig;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.list_fragment.models.ListFragmentAdapterModel;
import com.sbizzera.go4lunch.list_fragment.models.ListFragmentModel;
import com.sbizzera.go4lunch.list_fragment.utils.DistanceComparator;
import com.sbizzera.go4lunch.list_fragment.utils.LikesComparator;
import com.sbizzera.go4lunch.list_fragment.utils.LunchCountComparator;
import com.sbizzera.go4lunch.list_fragment.utils.RestaurantNameComparator;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreRestaurant;
import com.sbizzera.go4lunch.repositories.google_places.models.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.AddressComponent;
import com.sbizzera.go4lunch.repositories.google_places.models.places_place_details_models.DetailResult;
import com.sbizzera.go4lunch.repositories.CurrentGPSLocationRepo;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.google_places.GooglePlacesRepo;
import com.sbizzera.go4lunch.repositories.SortTypeChosenRepo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ListFragmentViewModel extends ViewModel {


    private GooglePlacesRepo mGooglePlacesRepo;
    private FireStoreRepo mFireStoreRepo;
    private SortTypeChosenRepo mSortTypeChosenRepo;
    private final Context mContext;

    private MediatorLiveData<ListFragmentModel> mModelMLD = new MediatorLiveData<>();
    private MutableLiveData<Map<String, DetailResult>> mDetailsMapLD = new MutableLiveData<>();

    private List<String> listOfMadeRequests = new ArrayList<>();
    private LiveData<Location> mCurrentGPSLocationLD;


    public ListFragmentViewModel(
            CurrentGPSLocationRepo currentGPSLocationRepo,
            GooglePlacesRepo googlePlacesRepo,
            FireStoreRepo fireStoreRepo,
            SortTypeChosenRepo sortTypeChosenRepo,
            Context context
    ){
        this.mGooglePlacesRepo = googlePlacesRepo;
        this.mFireStoreRepo = fireStoreRepo;
        mSortTypeChosenRepo = sortTypeChosenRepo;
        mDetailsMapLD.setValue(new HashMap<>());
        mCurrentGPSLocationLD = currentGPSLocationRepo.getCurrentGPSLocationLD();
        mContext = context;
        wireUpMediator();
    }

    public LiveData<ListFragmentModel> getModel() {
        return mModelMLD;
    }

    private void wireUpMediator() {

        LiveData<List<FireStoreRestaurant>> knownRestaurantsLiveData = mFireStoreRepo.getAllKnownRestaurants();
        LiveData<Integer> sortTypeChosenLD = mSortTypeChosenRepo.getSelectedChipID();


        mModelMLD.addSource(knownRestaurantsLiveData, knownRestaurants -> {
            combineSources(knownRestaurants, mDetailsMapLD.getValue(), sortTypeChosenLD.getValue());
        });

        mModelMLD.addSource(mDetailsMapLD, detailsMap -> {
            combineSources(knownRestaurantsLiveData.getValue(), detailsMap, sortTypeChosenLD.getValue());
        });

        mModelMLD.addSource(sortTypeChosenLD, sortTypeChosen -> {
            combineSources(knownRestaurantsLiveData.getValue(), mDetailsMapLD.getValue(), sortTypeChosen);
        });

    }

    private void combineSources(
            List<FireStoreRestaurant> knownRestaurants,
            Map<String,DetailResult> detailsMap,
            Integer sortTypeChose
    ) {

        List<NearbyPlace> nearbyPlaces = mGooglePlacesRepo.getNearbyCache();
        //Stop if no data in both sources
        if (nearbyPlaces == null && knownRestaurants == null) {
            return;
        }

        //Create the list of Id's to search details (nearby + firestore - detailsMap)
        List<String> listOfIds = new ArrayList<>();
        if (knownRestaurants != null) {
            for (FireStoreRestaurant restaurant : knownRestaurants) {
                if (!listOfMadeRequests.contains(restaurant.getRestaurantId())) {
                    listOfIds.add(restaurant.getRestaurantId());
                }
            }
        }
        if (nearbyPlaces != null) {
            for (NearbyPlace restaurant : nearbyPlaces) {
                if (!listOfMadeRequests.contains(restaurant.getId())) {
                    listOfIds.add(restaurant.getId());
                }
            }
        }

        //Fetch and and register event in ListOfMadeRequests
        for (String id : listOfIds) {
            new DetailResultAsyncTask(id, new WeakReference<>(this), mGooglePlacesRepo).execute();
            listOfMadeRequests.add(id);
        }

        //Send Existing Data To Model
        List<ListFragmentAdapterModel> listOfRestaurantToReturn = new ArrayList<>();
        List<String> fireStoreRestaurantsIdList = new ArrayList<>();

        if (knownRestaurants != null) {

            for (FireStoreRestaurant restaurant : knownRestaurants) {
                Timber.d("checking knownRestaurant %s", restaurant.getName());
                fireStoreRestaurantsIdList.add(restaurant.getRestaurantId());
                ListFragmentAdapterModel restaurantToReturn = null;
                DetailResult restaurantDetail = detailsMap.get(restaurant.getRestaurantId());
                if (restaurantDetail != null) {
                    String todayLunchCount = String.valueOf(restaurant.getLunchCount());
                    int star1Visibility = getStar1Visibility(restaurant);
                    int star2Visibility = getStar2Visibility(restaurant);
                    int star3Visibility = getStar3Visibility(restaurant);
                    int likeCount = getLikesCount(restaurant);
                    String address = getAdresseFromDetailResult(restaurantDetail);
                    String openHoursText = getOpenHoursTextFromDetailResult(restaurantDetail);
                    int openHoursColors = getOpenHoursTextColorFromDetailResult(restaurantDetail);
                    Double distanceDouble = getDistanceFromDetailResult(restaurantDetail);
                    int metersTextVisibility = getMetersTextVisibilityFromDistanceStr(distanceDouble);
                    String photoUrl = getPhotoUrlFromDerailResult(restaurantDetail);
                    restaurantToReturn = new ListFragmentAdapterModel(
                            restaurant.getName(),
                            restaurant.getRestaurantId(),
                            address,
                            openHoursText,
                            openHoursColors,
                            distanceDouble,
                            metersTextVisibility,
                            todayLunchCount,
                            star1Visibility,
                            star2Visibility,
                            star3Visibility,
                            likeCount,
                            photoUrl
                    );
                }
                if (restaurantToReturn != null) {
                    listOfRestaurantToReturn.add(restaurantToReturn);
                }
            }
        }

        if (nearbyPlaces != null) {
            for (NearbyPlace restaurant : nearbyPlaces) {
                if (!fireStoreRestaurantsIdList.contains(restaurant.getId())) {
                    ListFragmentAdapterModel restaurantToReturn = null;
                    DetailResult restaurantDetail = detailsMap.get(restaurant.getId());

                    if (restaurantDetail != null) {
                        String address = getAdresseFromDetailResult(restaurantDetail);
                        String openHoursText = getOpenHoursTextFromDetailResult(restaurantDetail);
                        int openHoursColors = getOpenHoursTextColorFromDetailResult(restaurantDetail);
                        Double distanceDouble = getDistanceFromDetailResult(restaurantDetail);
                        int metersTextVisibility = getMetersTextVisibilityFromDistanceStr(distanceDouble);
                        String todayLunchCount = "0";
                        int star1Visibility = View.INVISIBLE;
                        int star2Visibility = View.INVISIBLE;
                        int star3Visibility = View.INVISIBLE;
                        String photoUrl = getPhotoUrlFromDerailResult(restaurantDetail);
                        restaurantToReturn = new ListFragmentAdapterModel(
                                restaurant.getName(),
                                restaurant.getId(),
                                address,
                                openHoursText,
                                openHoursColors,
                                distanceDouble,
                                metersTextVisibility,
                                todayLunchCount,
                                star1Visibility,
                                star2Visibility,
                                star3Visibility,
                                0,
                                photoUrl
                        );
                    }
                    if (restaurantToReturn != null) {
                        listOfRestaurantToReturn.add(restaurantToReturn);
                    }
                }
            }
        }
        switch (sortTypeChose) {
            case R.id.likes_chip: {
                Collections.sort(listOfRestaurantToReturn, new LikesComparator());
                break;
            }
            case R.id.frequentation_chip: {
                Collections.sort(listOfRestaurantToReturn, new LunchCountComparator());
                break;
            }
            case R.id.name_chip: {
                Collections.sort(listOfRestaurantToReturn, new RestaurantNameComparator());
                break;
            }
            default: {
                Collections.sort(listOfRestaurantToReturn, new DistanceComparator());
                break;
            }
        }
        mModelMLD.setValue(new ListFragmentModel(listOfRestaurantToReturn, sortTypeChose));
    }

    private int getLikesCount(FireStoreRestaurant restaurant) {
        if (restaurant.getLikesIds() == null) {
            return 0;
        } else {
            return restaurant.getLikesIds().size();
        }
    }

    private int getMetersTextVisibilityFromDistanceStr(Double distance) {
        if (distance == null) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private String getPhotoUrlFromDerailResult(DetailResult restaurantDetail) {
        String photoUrl = null;
        if (restaurantDetail.getPhotosList() != null && restaurantDetail.getPhotosList().get(0) != null) {
            String photoRef = restaurantDetail.getPhotosList().get(0).getPhotoReference();
            photoUrl = new Uri.Builder().scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("place")
                    .appendPath("photo")
                    .appendQueryParameter("maxwidth", "200")
                    .appendQueryParameter("photoreference", photoRef)
                    .appendQueryParameter("key", BuildConfig.GOOGLE_API_KEY)
                    .toString();
        }
        return photoUrl;
    }

    private int getOpenHoursTextColorFromDetailResult(DetailResult restaurantDetail) {
        int openHourTextColor = R.color.quantum_grey500;
        if (restaurantDetail.getOpeningHours() != null && restaurantDetail.getOpeningHours().getOpenNow() != null) {
            if (restaurantDetail.getOpeningHours().getOpenNow()) {
                openHourTextColor = R.color.open;
            } else {
                openHourTextColor = R.color.closed;
            }
        }
        return openHourTextColor;
    }

    private String getOpenHoursTextFromDetailResult(DetailResult restaurantDetail) {
        String openHourText = mContext.getString(R.string.no_schedule_available);
        if (restaurantDetail.getOpeningHours() != null && restaurantDetail.getOpeningHours().getOpenNow() != null) {
            if (restaurantDetail.getOpeningHours().getOpenNow()) {
                openHourText = mContext.getString(R.string.open_now);
            } else {
                openHourText = mContext.getString(R.string.closed);
            }
        }
        return openHourText;
    }

    private String getAdresseFromDetailResult(DetailResult restaurantDetail) {
        String address = "";
        if (restaurantDetail.getAddressComponentList() != null) {
            List<AddressComponent> addressComponents = restaurantDetail.getAddressComponentList();
            if (addressComponents.get(0).getValue() != null) {
                address = addressComponents.get(0).getValue();
            }
            if (addressComponents.get(1).getValue() != null) {
                String streetName = addressComponents.get(1).getValue();
                address = address + ", " + streetName;
            }

        }
        return address;

    }

    private Double getDistanceFromDetailResult(DetailResult restaurantDetail) {
        Location currentGPSLocation = mCurrentGPSLocationLD.getValue();
        if (currentGPSLocation != null) {
            Double restaurantLat = restaurantDetail.getGeometry().getLocation().getLat();
            Double restaurantLng = restaurantDetail.getGeometry().getLocation().getLng();
            Location restaurantLocation = new Location("");
            restaurantLocation.setLatitude(restaurantLat);
            restaurantLocation.setLongitude(restaurantLng);
            double distanceInKm = currentGPSLocation.distanceTo(restaurantLocation) / 1000;
            return Math.round(distanceInKm * 10) / 10.0;
        }
        return null;
    }

    private int getStar1Visibility(FireStoreRestaurant restaurant) {
        if (restaurant.getLikesIds() != null) {
            if (restaurant.getLikesIds().size() > 0) {
                return View.VISIBLE;
            }
        }
        return View.INVISIBLE;
    }

    private int getStar2Visibility(FireStoreRestaurant restaurant) {
        if (restaurant.getLikesIds() != null) {
            if (restaurant.getLikesIds().size() > 1) {
                return View.VISIBLE;
            }
        }
        return View.INVISIBLE;
    }

    private int getStar3Visibility(FireStoreRestaurant restaurant) {
        if (restaurant.getLikesIds() != null) {
            if (restaurant.getLikesIds().size() > 2) {
                return View.VISIBLE;
            }
        }
        return View.INVISIBLE;
    }

    public void setSelectedChipID(int checkedId) {
        mSortTypeChosenRepo.setSelectedChipID(checkedId);
    }


    private static class DetailResultAsyncTask extends AsyncTask<Void, Void, DetailResult> {
        private String restaurantId;
        private WeakReference<ListFragmentViewModel> viewModelRef;
        private GooglePlacesRepo mGooglePlacesRepo;

        DetailResultAsyncTask(String restaurantId, WeakReference<ListFragmentViewModel> viewModelRef, GooglePlacesRepo googlePlacesRepo) {
            super();
            this.restaurantId = restaurantId;
            this.viewModelRef = viewModelRef;
            this.mGooglePlacesRepo = googlePlacesRepo;
        }

        @Override
        protected DetailResult doInBackground(Void... voids) {
            return mGooglePlacesRepo.getRestaurantDetailsByIdAsync(restaurantId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(DetailResult detailResult) {
            if (viewModelRef.get() != null) {
                Map<String,DetailResult> map = viewModelRef.get().mDetailsMapLD.getValue();
                if (map != null) {
                    map.put(restaurantId, detailResult);
                }
                viewModelRef.get().mDetailsMapLD.setValue(map);
            }
        }
    }
}
