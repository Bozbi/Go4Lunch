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

    private MutableLiveData<Boolean> isRestaurantLikedByUser = new MutableLiveData<>();
    private MutableLiveData<Integer> restaurantLikesCount = new MutableLiveData<>();
    private MutableLiveData<Boolean> restaurantTodayUserChoice = new MutableLiveData<>();


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
                                    }else{
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

    public void setLikeAndChoiceListener(String restaurantId) {
        //TODO Factorise
        //CheckForCurrentUser
        likes.whereEqualTo("restaurantID", restaurantId).whereEqualTo("userID", currentUserId).addSnapshotListener(
                (snapshot, e) -> {
                    if (snapshot.getDocuments().size() == 0) {
                        isRestaurantLikedByUser.postValue(false);
                    } else {
                        isRestaurantLikedByUser.postValue(true);
                    }
                }
        );
        //CheckAllLikes
        likes.whereEqualTo("restaurantID", restaurantId).addSnapshotListener(
                (snapshot, e) -> {
                    restaurantLikesCount.postValue(snapshot.getDocuments().size());
                }
        );
        //CheckChoice
        dailyChoice.whereEqualTo("date", LocalDate.now().toString())
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("restaurantId", restaurantId)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot.getDocuments().size() == 0) {
                        restaurantTodayUserChoice.postValue(false);
                    } else {
                        restaurantTodayUserChoice.postValue(true);
                    }
                });

    }

    public LiveData<Boolean> getIsRestaurantLikedByUserLiveData() {
        return isRestaurantLikedByUser;
    }

    public LiveData<Integer> getRestaurantLikeCount() {
        return restaurantLikesCount;
    }

    public LiveData<Boolean> getRestaurantTodayUserChoice() {
        return restaurantTodayUserChoice;
    }


}
