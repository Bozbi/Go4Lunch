package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;
import com.sbizzera.go4lunch.services.CurrentGPSLocationRepo;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.utils.Commons;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ListFragmentViewModel extends ViewModel {


    private CurrentGPSLocationRepo mCurrentGPSLocationRepo;
    private GooglePlacesService mGooglePlacesService;
    private FireStoreService mFireStoreService;

    private MediatorLiveData<ListFragmentModel> mModelMLD = new MediatorLiveData<>();
    private MutableLiveData<Map<String, DetailsResponse.DetailResult>> mDetailsMapLD = new MutableLiveData<>();

    private List<String> listOfMadeRequests = new ArrayList<>();


    public ListFragmentViewModel(CurrentGPSLocationRepo mCurrentGPSLocationRepo, GooglePlacesService googlePlacesService, FireStoreService fireStoreService) {
        this.mCurrentGPSLocationRepo = mCurrentGPSLocationRepo;
        this.mGooglePlacesService = googlePlacesService;
        this.mFireStoreService = fireStoreService;
        mDetailsMapLD.setValue(new HashMap<>());
        wireUpMediator();
    }

    public LiveData<ListFragmentModel> getModel() {
        return mModelMLD;
    }

    private void wireUpMediator() {
        LiveData<List<FireStoreRestaurant>> knownRestaurantsLiveData = mFireStoreService.getAllKnownRestaurants();

        mModelMLD.addSource(knownRestaurantsLiveData, knownRestaurants -> {
            combineSources(knownRestaurants, mDetailsMapLD.getValue());
        });

        mModelMLD.addSource(mDetailsMapLD, detailsMap -> {
            combineSources(knownRestaurantsLiveData.getValue(), detailsMap);
        });

    }

    private void combineSources(
            List<FireStoreRestaurant> knownRestaurants,
            Map<String, DetailsResponse.DetailResult> detailsMap) {

        List<NearbyPlace> nearbyPlaces = mGooglePlacesService.getNearbyCache();
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
            new DetailResultAsyncTask(id, new WeakReference<>(this), mGooglePlacesService).execute();
            listOfMadeRequests.add(id);
        }

        //Send Existing Data To Model
        List<ListFragmentAdapterModel> listOfRestaurantToReturn = new ArrayList<>();
        List<String> fireStoreRestaurantsIdList = new ArrayList<>();

        if (knownRestaurants != null)
            for (FireStoreRestaurant restaurant : knownRestaurants) {
                fireStoreRestaurantsIdList.add(restaurant.getRestaurantId());
                String address = null;
                String openHoursText = null;
                int openHoursColors = R.color.primaryTextColor;
                String distanceStr = null;
                int metersTextVisibility = View.INVISIBLE;
                String todayLunchCount = String.valueOf(restaurant.getTodaysLunches());
                int star1Visibility = getStar1Visibility(restaurant);
                int star2Visibility = getStar2Visibility(restaurant);
                int star3Visibility = getStar3Visibility(restaurant);
                String photoUrl = null;
                DetailsResponse.DetailResult restaurantDetail = detailsMap.get(restaurant.getRestaurantId());
                if (restaurantDetail != null) {
                    address = getAdresseFromDetailResult(restaurantDetail);
                    openHoursText = getOpenHoursTextFromDetailResult(restaurantDetail);
                    openHoursColors = getOpenHoursTextColorFromDetailResult(restaurantDetail);
                    distanceStr = getDistanceFromDetailResult(restaurantDetail);
                    metersTextVisibility = getMetersTextVisibilityFromDistanceStr(distanceStr);
                    photoUrl = getPhotoUrlFromDerailResult(restaurantDetail);
                }
                ListFragmentAdapterModel restaurantToReturn = new ListFragmentAdapterModel(
                        restaurant.getName(),
                        restaurant.getRestaurantId(),
                        address,
                        openHoursText,
                        openHoursColors,
                        distanceStr,
                        metersTextVisibility,
                        todayLunchCount,
                        star1Visibility,
                        star2Visibility,
                        star3Visibility,
                        photoUrl
                );
                listOfRestaurantToReturn.add(restaurantToReturn);

            }

        if (nearbyPlaces != null) {
            for (NearbyPlace restaurant : nearbyPlaces) {
                if (!fireStoreRestaurantsIdList.contains(restaurant.getId())) {
                    String address = null;
                    String openHoursText = null;
                    int openHoursColors = R.color.primaryTextColor;
                    String distanceStr = null;
                    int metersTextVisibility = View.INVISIBLE;
                    String todayLunchCount = "0";
                    int star1Visibility = View.INVISIBLE;
                    int star2Visibility = View.INVISIBLE;
                    int star3Visibility = View.INVISIBLE;
                    String photoUrl = null;
                    DetailsResponse.DetailResult restaurantDetail = detailsMap.get(restaurant.getId());
                    if (restaurantDetail != null) {
                        address = getAdresseFromDetailResult(restaurantDetail);
                        openHoursText = getOpenHoursTextFromDetailResult(restaurantDetail);
                        openHoursColors = getOpenHoursTextColorFromDetailResult(restaurantDetail);
                        distanceStr = getDistanceFromDetailResult(restaurantDetail);
                        metersTextVisibility = getMetersTextVisibilityFromDistanceStr(distanceStr);
                        photoUrl = getPhotoUrlFromDerailResult(restaurantDetail);
                    }
                    ListFragmentAdapterModel restaurantToReturn = new ListFragmentAdapterModel(
                            restaurant.getName(),
                            restaurant.getId(),
                            address,
                            openHoursText,
                            openHoursColors,
                            distanceStr,
                            metersTextVisibility,
                            todayLunchCount,
                            star1Visibility,
                            star2Visibility,
                            star3Visibility,
                            photoUrl
                    );
                    listOfRestaurantToReturn.add(restaurantToReturn);
                }
            }
        }
        Collections.sort(listOfRestaurantToReturn, new DistanceComparator());

        mModelMLD.setValue(new ListFragmentModel(listOfRestaurantToReturn));
    }

    private int getMetersTextVisibilityFromDistanceStr(String distanceStr) {
        if (distanceStr == null) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    private String getPhotoUrlFromDerailResult(DetailsResponse.DetailResult restaurantDetail) {
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
                    .appendQueryParameter("key", Commons.PLACES_API_KEY)
                    .toString();
        }
        return photoUrl;
    }

    private int getOpenHoursTextColorFromDetailResult(DetailsResponse.DetailResult restaurantDetail) {
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

    private String getOpenHoursTextFromDetailResult(DetailsResponse.DetailResult restaurantDetail) {
        String openHourText = "No Schedule available";
        if (restaurantDetail.getOpeningHours() != null && restaurantDetail.getOpeningHours().getOpenNow() != null) {
            if (restaurantDetail.getOpeningHours().getOpenNow()) {
                openHourText = "Open Now";
            } else {
                openHourText = "Closed";
            }
        }
        return openHourText;
    }

    private String getAdresseFromDetailResult(DetailsResponse.DetailResult restaurantDetail) {
        String address = "";
        if (restaurantDetail.getAddressComponentList() != null) {
            List<DetailsResponse.DetailResult.AddressComponent> addressComponents = restaurantDetail.getAddressComponentList();
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

    private String getDistanceFromDetailResult(DetailsResponse.DetailResult restaurantDetail) {
        Location currentGPSLocation = mCurrentGPSLocationRepo.getCurrentGPSLocationLD().getValue();
        if (currentGPSLocation != null) {
            Double restaurantLat = restaurantDetail.getGeometry().getLocation().getLat() ;
            Double restaurantLng = restaurantDetail.getGeometry().getLocation().getLng() ;
            Location restaurantLocation = new Location("");
            restaurantLocation.setLatitude(restaurantLat);
            restaurantLocation.setLongitude(restaurantLng);
            double distanceInKm = currentGPSLocation.distanceTo(restaurantLocation)/1000;
            double distanceRounded = Math.round(distanceInKm*10)/10.0;
            return String.valueOf(distanceRounded);
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


    private static class DetailResultAsyncTask extends AsyncTask<Void, Void, DetailsResponse.DetailResult> {
        private String restaurantId;
        private WeakReference<ListFragmentViewModel> viewModelRef;
        private GooglePlacesService googlePlacesService;

        DetailResultAsyncTask(String restaurantId, WeakReference<ListFragmentViewModel> viewModelRef, GooglePlacesService googlePlacesService) {
            super();
            this.restaurantId = restaurantId;
            this.viewModelRef = viewModelRef;
            this.googlePlacesService = googlePlacesService;
        }

        @Override
        protected DetailsResponse.DetailResult doInBackground(Void... voids) {
            return googlePlacesService.getRestaurantDetailsByIdAsync(restaurantId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(DetailsResponse.DetailResult detailResult) {
            if (viewModelRef.get() != null) {
                Map<String, DetailsResponse.DetailResult> map = viewModelRef.get().mDetailsMapLD.getValue();
                if (map != null) {
                    map.put(restaurantId, detailResult);
                }
                viewModelRef.get().mDetailsMapLD.setValue(map);
            }
        }
    }
}
