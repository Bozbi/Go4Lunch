package com.sbizzera.go4lunch.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreLike;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreUser;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreUserDailyChoice;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse.DetailResult;

import org.threeten.bp.LocalDate;


public class FireStoreService {

    private static final String TAG = "FireStoreService";
    private String currentUserId = FirebaseAuthService.getCurrentUserId();
    private FirebaseUser currentUser = FirebaseAuthService.getUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    private CollectionReference restaurants = db.collection("restaurants");
    private CollectionReference likes = db.collection("likes");
    private CollectionReference dailyChoice = db.collection("dailyChoice");


    public void addRestaurantToFireStore(DetailResult restaurant) {
        restaurants.add(new FireStoreRestaurant(
                restaurant.getPlaceId(),
                restaurant.getName(),
                restaurant.getGeometry().getLocation().getLat(),
                restaurant.getGeometry().getLocation().getLng()
        ));
    }

    public void addUserToFireStore() {
        users.add(new FireStoreUser(
                currentUserId,
                currentUser.getDisplayName(),
                currentUser.getPhotoUrl().toString()
        ));
    }

    public void handleUserInFireStore() {
        users.whereEqualTo("userId", currentUserId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.getDocuments().size() == 0) {
                addUserToFireStore();
            }
        });
    }


    public void updateRestaurantLike(DetailResult restaurant) {
        //TODO Factorise
        //Check if Restaurant in Likes for this User
        likes.whereEqualTo("restaurantID", restaurant.getPlaceId())
                .whereEqualTo("userID", currentUserId)
                .get().addOnSuccessListener(querySnapshot1 -> {
            if (querySnapshot1.getDocuments().size() == 1) {
                //Remove Like
                likes.document(querySnapshot1.getDocuments().get(0).getId()).delete();
            } else {
                //check if Retaurant exists
                restaurants.whereEqualTo("restaurantID", restaurant.getPlaceId()).get()
                        .addOnSuccessListener(querySnapshot2 -> {
                            if (querySnapshot2.getDocuments().size() == 0) {
                                //Add Restaurant
                                addRestaurantToFireStore(restaurant);
                                //Add Like
                                likes.add(new FireStoreLike(
                                        currentUserId,
                                        restaurant.getPlaceId()
                                ));
                            } else {
                                likes.add(new FireStoreLike(
                                        currentUserId,
                                        restaurant.getPlaceId()
                                ));
                            }
                        });
            }
        });
    }

    public void updateRestaurantChoice(DetailResult restaurant) {
        //Todo Factorise
        dailyChoice.whereEqualTo("userId", currentUserId)
                .whereEqualTo("date", LocalDate.now().toString()).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.getDocuments().size() == 1) {
                        dailyChoice.whereEqualTo("restaurantId", restaurant.getPlaceId())
                                .whereEqualTo("userId", currentUserId)
                                .whereEqualTo("date", LocalDate.now().toString()).get()
                                .addOnSuccessListener(querySnapshot2 -> {
                                    if (querySnapshot2.getDocuments().size() == 1) {
                                        dailyChoice.document(querySnapshot2.getDocuments().get(0).getId()).delete();
                                    } else {
                                        dailyChoice.document(querySnapshot.getDocuments().get(0).getId()).delete();
                                        dailyChoice.add(new FireStoreUserDailyChoice(
                                                LocalDate.now().toString(),
                                                currentUserId,
                                                restaurant.getPlaceId()
                                        ));
                                    }
                                });
                    } else {
                        //check if Retaurant exists
                        restaurants.whereEqualTo("restaurantID", restaurant.getPlaceId()).get()
                                .addOnSuccessListener(querySnapshot3 -> {
                                    if (querySnapshot3.getDocuments().size() == 0) {
                                        //Add Restaurant
                                        addRestaurantToFireStore(restaurant);
                                        //Add Like
                                        dailyChoice.add(new FireStoreUserDailyChoice(
                                                LocalDate.now().toString(),
                                                currentUserId,
                                                restaurant.getPlaceId()
                                        ));
                                    } else {
                                        dailyChoice.add(new FireStoreUserDailyChoice(
                                                LocalDate.now().toString(),
                                                currentUserId,
                                                restaurant.getPlaceId()
                                        ));
                                    }
                                });
                    }
                });
    }


    public LiveData<Boolean> getIsRestaurantLikedByUserLiveData(String id) {
        MutableLiveData<Boolean> isLikedMutable = new MutableLiveData<>();
        likes.whereEqualTo("restaurantID", id).whereEqualTo("userID", currentUserId).addSnapshotListener(
                (snapshot, e) -> {
                    if (snapshot.getDocuments().size() == 0) {
                        isLikedMutable.postValue(false);
                    } else {
                        isLikedMutable.postValue(true);
                    }
                }
        );
        return isLikedMutable;
    }

    public LiveData<Integer> getRestaurantLikeCount(String id) {
        MutableLiveData<Integer> likeCountLiveData = new MutableLiveData<>();
        likes.whereEqualTo("restaurantID", id).addSnapshotListener(
                (snapshot, e) -> {
                    if (snapshot != null) {
                            likeCountLiveData.postValue(snapshot.getDocuments().size());
                    }
                }
        );
        return likeCountLiveData;
    }

    public LiveData<Boolean> getRestaurantTodayUserChoice(String id) {
        MutableLiveData<Boolean> isChosenByUserLiveData = new MutableLiveData<>();
        dailyChoice.whereEqualTo("date", LocalDate.now().toString())
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("restaurantId", id)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null) {
                        if (snapshot.getDocuments().size() == 0) {
                            isChosenByUserLiveData.postValue(false);
                        } else {
                            isChosenByUserLiveData.postValue(true);
                        }
                    }
                });
        return isChosenByUserLiveData;
    }


}
