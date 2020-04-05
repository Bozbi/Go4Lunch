package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.places_nearby_models.NearbyPlace;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.services.GooglePlacesService;
import com.sbizzera.go4lunch.services.LocationService;
import com.sbizzera.go4lunch.utils.Commons;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ListFragmentViewModel extends ViewModel {


    private LocationService locator;
    private GooglePlacesService googlePlacesService;
    private FireStoreService fireStoreService;

    private MediatorLiveData<ListFragmentModel> modelLiveData = new MediatorLiveData<>();

    private MutableLiveData<Map<String, DetailsResponse.DetailResult>> detailsMapLD = new MutableLiveData<>();

    private List<String> listOfMadeRequests = new ArrayList<>();

    private LiveData<Location> locationLiveData;

    public ListFragmentViewModel(LocationService locator, GooglePlacesService googlePlacesService, FireStoreService fireStoreService) {
        this.locator = locator;
        this.googlePlacesService = googlePlacesService;
        this.fireStoreService = fireStoreService;
        detailsMapLD.setValue(new HashMap<>());
        wireUpMediator();
    }

    public LiveData<ListFragmentModel> getModel() {
        return modelLiveData;
    }

    private void wireUpMediator() {
        locationLiveData = locator.getLocation();
        LiveData<List<NearbyPlace>> nearbyRestaurantsLiveData = Transformations.switchMap(locationLiveData,
                location -> googlePlacesService.getNearbyRestaurants(Go4LunchUtils.locationToString(location)));
        LiveData<List<FireStoreRestaurant>> knownRestaurantsLiveData = fireStoreService.getAllKnownRestaurants();

        modelLiveData.addSource(nearbyRestaurantsLiveData, nearbyPlaces -> {
            combineSources(nearbyPlaces, knownRestaurantsLiveData.getValue(), detailsMapLD.getValue());
        });

        modelLiveData.addSource(knownRestaurantsLiveData, knownRestaurants -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), knownRestaurants, detailsMapLD.getValue());
        });

        modelLiveData.addSource(detailsMapLD, detailsMap -> {
            combineSources(nearbyRestaurantsLiveData.getValue(), knownRestaurantsLiveData.getValue(), detailsMap);
        });

    }

    private void combineSources(
            List<NearbyPlace> nearbyPlaces,
            List<FireStoreRestaurant> knownRestaurants,
            Map<String, DetailsResponse.DetailResult> detailsMap) {

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
            new DetailResultAsyncTask(id, new WeakReference<>(this), googlePlacesService).execute();
            listOfMadeRequests.add(id);
        }

        //Send Existing Data To Model
        List<ListFragmentAdapterModel> listOfRestaurantToReturn = new ArrayList<>();
        List<String> fireStoreRestaurantsIdList = new ArrayList<>();

        if (knownRestaurants != null)
            for (FireStoreRestaurant restaurant : knownRestaurants) {
                int distance = getDistanceFromLatAndLng(restaurant.getLat(), restaurant.getLng());
                if (distance >= 0 && distance < 3000) {
                    fireStoreRestaurantsIdList.add(restaurant.getRestaurantId());
                    String address = null;
                    String openHoursText = null;
                    int openHoursColors = R.color.primaryTextColor;
                    String distanceStr = null;
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
                        photoUrl = getPhotoUrlFromDerailResult(restaurantDetail);
                    }
                    ListFragmentAdapterModel restaurantToReturn = new ListFragmentAdapterModel(
                            restaurant.getName(),
                            restaurant.getRestaurantId(),
                            address,
                            openHoursText,
                            openHoursColors,
                            distanceStr,
                            todayLunchCount,
                            star1Visibility,
                            star2Visibility,
                            star3Visibility,
                            photoUrl
                    );
                    listOfRestaurantToReturn.add(restaurantToReturn);
                }
            }

        if (nearbyPlaces != null) {
            for (NearbyPlace restaurant : nearbyPlaces) {
                if (!fireStoreRestaurantsIdList.contains(restaurant.getId())) {
                    int distance = getDistanceFromLatAndLng(restaurant.getLat(), restaurant.getLng());
                    if (distance >= 0 && distance < 3000) {
                        String address = null;
                        String openHoursText = null;
                        int openHoursColors = R.color.primaryTextColor;
                        String distanceStr = null;
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
                            photoUrl = getPhotoUrlFromDerailResult(restaurantDetail);
                        }
                        ListFragmentAdapterModel restaurantToReturn = new ListFragmentAdapterModel(
                                restaurant.getName(),
                                restaurant.getId(),
                                address,
                                openHoursText,
                                openHoursColors,
                                distanceStr,
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
        }
        Collections.sort(listOfRestaurantToReturn, new DistanceComparator());

        modelLiveData.setValue(new ListFragmentModel(listOfRestaurantToReturn));
    }

    private int getDistanceFromLatAndLng(Double lat, Double lng) {
        Location userLocation = locationLiveData.getValue();
        Location restaurantLocation = new Location("");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);
        if (userLocation != null) {
            int distance = (int) userLocation.distanceTo(restaurantLocation);
            return (int) userLocation.distanceTo(restaurantLocation);
        }
        return -1;
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
        Location userLocation = locationLiveData.getValue();
        Double restaurantLat = restaurantDetail.getGeometry().getLocation().getLat();
        Double restaurantLng = restaurantDetail.getGeometry().getLocation().getLng();
        Location restaurantLocation = new Location("");
        restaurantLocation.setLatitude(restaurantLat);
        restaurantLocation.setLongitude(restaurantLng);
        if (userLocation != null) {
            int distance = (int) userLocation.distanceTo(restaurantLocation);
            return String.valueOf(distance);
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
        private static int count;

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
            count++;
            Timber.d("Request #%s", count);
        }

        @Override
        protected void onPostExecute(DetailsResponse.DetailResult detailResult) {
            if (viewModelRef.get() != null) {
                Map<String, DetailsResponse.DetailResult> map = viewModelRef.get().detailsMapLD.getValue();
                map.put(restaurantId, detailResult);
                viewModelRef.get().detailsMapLD.setValue(map);
            }
        }
    }
}
