package com.sbizzera.go4lunch.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreUser;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailResult;


import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;


public class FireStoreRepo {

    private static FireStoreRepo sFireStoreRepo;

    private FireStoreRepo(){

    }

    public static FireStoreRepo getInstance(){
        if (sFireStoreRepo==null){
            sFireStoreRepo = new FireStoreRepo();
        }
        return sFireStoreRepo;
    }



    private CollectionReference users = FirebaseFirestore.getInstance().collection("users");
    private CollectionReference restaurants = FirebaseFirestore.getInstance().collection("restaurants");
    private CollectionReference dates = FirebaseFirestore.getInstance().collection("dates");


    //Insert or Update user in FiresStore
    public void updateUserInDb(FirebaseUser currentUser) {
        String photoUrl = null;
        if (currentUser.getPhotoUrl() != null) {
            photoUrl = currentUser.getPhotoUrl().toString();
        }

        users.document(currentUser.getUid()).set(new FireStoreUser(
                currentUser.getUid(),
                currentUser.getDisplayName(),
                photoUrl
        ));
    }


    public void updateRestaurantLike(DetailResult restaurant,String currentUserId) {
        restaurants.document(restaurant.getPlaceId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.getData() != null) {
                //update like
                FireStoreRestaurant fetchedRestaurant = documentSnapshot.toObject(FireStoreRestaurant.class);
                if (fetchedRestaurant != null) {
                    List<String> likes = new ArrayList<>();
                    if (fetchedRestaurant.getLikesIds() != null) {
                        likes = fetchedRestaurant.getLikesIds();
                    }
                    if (likes.contains(currentUserId)) {
                        restaurants.document(restaurant.getPlaceId()).update("likesIds", FieldValue.arrayRemove(currentUserId));
                    } else {
                        restaurants.document(restaurant.getPlaceId()).update("likesIds", FieldValue.arrayUnion(currentUserId));
                    }
                }
            } else {
                //create restaurant and add like
                FireStoreRestaurant restaurantToAdd = new FireStoreRestaurant(
                        restaurant.getPlaceId(),
                        restaurant.getName(),
                        restaurant.getGeometry().getLocation().getLat(),
                        restaurant.getGeometry().getLocation().getLng()
                );
                List<String> likesIds = new ArrayList<>();
                likesIds.add(currentUserId);
                restaurantToAdd.setLikesIds(likesIds);
                restaurants.document(restaurant.getPlaceId()).set(restaurantToAdd);
            }
        });

    }

    public void updateRestaurantChoice(DetailResult restaurant,FirebaseUser currentUser) {
        String photoURl= null;
        if (currentUser.getPhotoUrl()!=null){
            photoURl = currentUser.getPhotoUrl().toString();
        }
        FireStoreLunch lunchToAdd = new FireStoreLunch(
                currentUser.getUid(),
                currentUser.getDisplayName(),
                photoURl,
                restaurant.getPlaceId(),
                restaurant.getName()
        );
        restaurants.document(restaurant.getPlaceId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.getData() != null) {
                //restaurant exists in Database
                updateLunch(restaurant, lunchToAdd,currentUser.getUid());

            } else {
                //create restaurant add lunch and update count
                FireStoreRestaurant restaurantToAdd = new FireStoreRestaurant(
                        restaurant.getPlaceId(),
                        restaurant.getName(),
                        restaurant.getGeometry().getLocation().getLat(),
                        restaurant.getGeometry().getLocation().getLng()
                );
                restaurants.document(restaurant.getPlaceId()).set(restaurantToAdd).addOnSuccessListener((newRestaurant) -> {
                    updateLunch(restaurant, lunchToAdd,currentUser.getUid());
                });

            }
        });
    }

    private void updateLunch(DetailResult restaurant, FireStoreLunch lunchToAdd,String currentUserId) {
        String localDateOfNowToString = LocalDate.now().toString();
        dates.document(localDateOfNowToString).collection("lunches").document(currentUserId).get().addOnSuccessListener(lunch -> {
            if (lunch.exists()) {
                FireStoreLunch existingLunch = lunch.toObject(FireStoreLunch.class);
                if (existingLunch.getRestaurantId().equals(restaurant.getPlaceId())) {
                    dates.document(localDateOfNowToString).collection("lunches").document(currentUserId).delete();
                    restaurants.document(existingLunch.getRestaurantId()).update("lunchCount", FieldValue.increment(-1));
                } else {
                    dates.document(localDateOfNowToString).collection("lunches").document(currentUserId).update("restaurantId", restaurant.getPlaceId(), "restaurantName", restaurant.getName());
                    restaurants.document(existingLunch.getRestaurantId()).update("lunchCount", FieldValue.increment(-1));
                    restaurants.document(restaurant.getPlaceId()).update("lunchCount", FieldValue.increment(1));
                }
            } else {
                dates.document(localDateOfNowToString).collection("lunches").document(currentUserId).set(lunchToAdd);
                restaurants.document(restaurant.getPlaceId()).update("lunchCount", FieldValue.increment(1));
            }
        });
    }


    public LiveData<Boolean> isRestaurantLikedByUser(String id,String currentUserId) {
        MutableLiveData<Boolean> isLikedLiveData = new MutableLiveData<>();
        restaurants.whereEqualTo("restaurantId", id).whereArrayContains("likesIds", currentUserId).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() == 0) {
                isLikedLiveData.postValue(false);
            } else {
                isLikedLiveData.postValue(true);
            }
        });
        return isLikedLiveData;
    }

    public LiveData<Integer> getRestaurantLikesCount(String id) {
        MutableLiveData<Integer> likesCountLiveData = new MutableLiveData<>();
        restaurants.document(id).addSnapshotListener((documentSnapshot, e) -> {

            int likesCount = 0;

            if (documentSnapshot != null) {
                FireStoreRestaurant fetchedRestaurant = documentSnapshot.toObject(FireStoreRestaurant.class);
                if (fetchedRestaurant != null) {
                    if (fetchedRestaurant.getLikesIds() != null) {
                        likesCount = fetchedRestaurant.getLikesIds().size();
                    }
                }
            }

            likesCountLiveData.postValue(likesCount);
        });
        return likesCountLiveData;
    }

    public LiveData<Boolean> isRestaurantChosenByUserToday(String id,String currentUserId) {
        MutableLiveData<Boolean> isRestaurantChosenByUserTodayLiveData = new MutableLiveData<>();
        dates.document(LocalDate.now().toString()).collection("lunches")
                .document(currentUserId)
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (documentSnapshots != null && documentSnapshots.exists() && documentSnapshots.getData() != null && documentSnapshots.getData().containsValue(id)) {
                        isRestaurantChosenByUserTodayLiveData.postValue(true);
                    } else {
                        isRestaurantChosenByUserTodayLiveData.postValue(false);
                    }
                });

        return isRestaurantChosenByUserTodayLiveData;
    }

    public LiveData<List<FireStoreUser>> getTodayListOfUsers(String id) {
        MutableLiveData<List<FireStoreUser>> listUsersLiveData = new MutableLiveData<>();
        List<FireStoreUser> usersToSend = new ArrayList<>();
        if (id != null) {
            dates.document(LocalDate.now().toString()).collection("lunches").whereEqualTo("restaurantId", id).addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    usersToSend.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        FireStoreUser userToAdd = document.toObject(FireStoreUser.class);
                        usersToSend.add(userToAdd);
                    }
                    listUsersLiveData.postValue(usersToSend);
                } else {
                    listUsersLiveData.postValue(null);
                }
            });
        }
        return listUsersLiveData;
    }

    public LiveData<List<FireStoreUser>> getAllUsers() {
        MutableLiveData<List<FireStoreUser>> allUsersLiveData = new MutableLiveData<>();
        users.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                List<FireStoreUser> usersList = new ArrayList<>();
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    FireStoreUser user = queryDocumentSnapshots.getDocuments().get(i).toObject(FireStoreUser.class);
                    usersList.add(user);
                }
                allUsersLiveData.postValue(usersList);
            }
        });

        return allUsersLiveData;
    }

    public LiveData<List<FireStoreLunch>> getAllTodaysLunches() {
        MutableLiveData<List<FireStoreLunch>> allTodaysLunchesLiveData = new MutableLiveData<>();
        dates.document(LocalDate.now().toString()).collection("lunches").addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<FireStoreLunch> lunchList = new ArrayList<>();
            for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                FireStoreLunch lunch = queryDocumentSnapshots.getDocuments().get(i).toObject(FireStoreLunch.class);
                lunchList.add(lunch);
            }
            allTodaysLunchesLiveData.postValue(lunchList);
        });
        return allTodaysLunchesLiveData;
    }

    public LiveData<List<FireStoreRestaurant>> getAllKnownRestaurants() {
        MutableLiveData<List<FireStoreRestaurant>> allKnownRestaurantsLiveData = new MutableLiveData<>();
        List<FireStoreRestaurant> allRestaurantsToReturn = new ArrayList<>();
        restaurants.whereGreaterThan("lunchCount", 0).get().addOnSuccessListener(allRestaurants -> {
            for (DocumentSnapshot restaurant : allRestaurants) {
                FireStoreRestaurant restaurantToAdd = restaurant.toObject(FireStoreRestaurant.class);
                allRestaurantsToReturn.add(restaurantToAdd);
            }
            allKnownRestaurantsLiveData.postValue(allRestaurantsToReturn);
        });
        return allKnownRestaurantsLiveData;
    }


    public LiveData<FireStoreLunch> getUserLunch(String currentUserId) {
        MutableLiveData<FireStoreLunch> todayUserLunchLD = new MutableLiveData<>();
        dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.toObject(FireStoreLunch.class) != null) {
                todayUserLunchLD.postValue(documentSnapshot.toObject(FireStoreLunch.class));
            } else {
                todayUserLunchLD.postValue(null);
            }
        });
        return todayUserLunchLD;
    }
}
