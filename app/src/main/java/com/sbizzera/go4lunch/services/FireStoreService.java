package com.sbizzera.go4lunch.services;

import android.util.Log;

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
    private String currentUserId = FirebaseAuthService.getUser().getUid();
    private FirebaseUser currentUser = FirebaseAuthService.getUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference allUsers = db.collection("users");
    private CollectionReference restaurants = db.collection("restaurants");
    private CollectionReference dateNode = db.collection("date");


    public void addRestaurantToFireStore(DetailResult restaurant) {
        restaurants.add(new FireStoreRestaurant(
                restaurant.getPlaceId(),
                restaurant.getName(),
                restaurant.getGeometry().getLocation().getLat(),
                restaurant.getGeometry().getLocation().getLng()
        ));
    }


    //Insert or Update user in MainUsers collection
    public void updateUserInDb() {
        String photoUrl = null;
        if (currentUser.getPhotoUrl() != null) {
            photoUrl = currentUser.getPhotoUrl().toString();
        }

        allUsers.document(currentUserId).set(new FireStoreUser(
                currentUserId,
                currentUser.getDisplayName(),
                photoUrl
        ));
    }


//    public void updateRestaurantLike(DetailResult restaurant) {
//        //TODO Factorise
//        //Check if Restaurant in Likes for this User
//        likes.whereEqualTo("restaurantID", restaurant.getPlaceId())
//                .whereEqualTo("userID", currentUserId)
//                .get().addOnSuccessListener(querySnapshot1 -> {
//            if (querySnapshot1.getDocuments().size() == 1) {
//                //Remove Like
//                likes.document(querySnapshot1.getDocuments().get(0).getId()).delete();
//            } else {
//                //check if Retaurant exists
//                restaurants.whereEqualTo("restaurantID", restaurant.getPlaceId()).get()
//                        .addOnSuccessListener(querySnapshot2 -> {
//                            if (querySnapshot2.getDocuments().size() == 0) {
//                                //Add Restaurant
//                                addRestaurantToFireStore(restaurant);
//                                //Add Like
//                                likes.add(new FireStoreLike(
//                                        currentUserId,
//                                        restaurant.getPlaceId()
//                                ));
//                            } else {
//                                likes.add(new FireStoreLike(
//                                        currentUserId,
//                                        restaurant.getPlaceId()
//                                ));
//                            }
//                        });
//            }
//        });
//    }

//    public void updateRestaurantChoice(DetailResult restaurant) {
//        //Todo Factorise
//        dailyChoice.whereEqualTo("userId", currentUserId)
//                .whereEqualTo("date", LocalDate.now().toString()).get()
//                .addOnSuccessListener(querySnapshot -> {
//                    if (querySnapshot.getDocuments().size() == 1) {
//                        dailyChoice.whereEqualTo("restaurantId", restaurant.getPlaceId())
//                                .whereEqualTo("userId", currentUserId)
//                                .whereEqualTo("date", LocalDate.now().toString()).get()
//                                .addOnSuccessListener(querySnapshot2 -> {
//                                    if (querySnapshot2.getDocuments().size() == 1) {
//                                        dailyChoice.document(querySnapshot2.getDocuments().get(0).getId()).delete();
//                                    } else {
//                                        dailyChoice.document(querySnapshot.getDocuments().get(0).getId()).delete();
//                                        dailyChoice.add(new FireStoreUserDailyChoice(
//                                                LocalDate.now().toString(),
//                                                currentUserId,
//                                                restaurant.getPlaceId()
//                                        ));
//                                    }
//                                });
//                    } else {
//                        //check if Retaurant exists
//                        restaurants.whereEqualTo("restaurantID", restaurant.getPlaceId()).get()
//                                .addOnSuccessListener(querySnapshot3 -> {
//                                    if (querySnapshot3.getDocuments().size() == 0) {
//                                        //Add Restaurant
//                                        addRestaurantToFireStore(restaurant);
//                                        //Add Like
//                                        dailyChoice.add(new FireStoreUserDailyChoice(
//                                                LocalDate.now().toString(),
//                                                currentUserId,
//                                                restaurant.getPlaceId()
//                                        ));
//                                    } else {
//                                        dailyChoice.add(new FireStoreUserDailyChoice(
//                                                LocalDate.now().toString(),
//                                                currentUserId,
//                                                restaurant.getPlaceId()
//                                        ));
//                                    }
//                                });
//                    }
//                });
//    }


    //Check if userId appears in subCollection "likes"
    public LiveData<Boolean> isRestaurantLikedByUser(String id) {
        MutableLiveData<Boolean> isLikedLiveData = new MutableLiveData<>();
        restaurants.document(id).collection("likes").document(currentUser.getUid()).addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                isLikedLiveData.postValue(true);
            } else {
                isLikedLiveData.postValue(false);
            }
        });
        return isLikedLiveData;
    }

    public LiveData<Integer> getRestaurantLikesCount(String id) {
        MutableLiveData<Integer> likesCountLiveData = new MutableLiveData<>();
        restaurants.document(id).collection("likes").addSnapshotListener((queryDocumentSnapshots, e) -> {
            likesCountLiveData.postValue(queryDocumentSnapshots.size());
        });
        return likesCountLiveData;
    }

    public LiveData<Boolean> isRestaurantChosenByUserToday(String id) {
        MutableLiveData<Boolean> isRestaurantChosenByUserTodayLiveData = new MutableLiveData<>();
        dateNode.document(LocalDate.now().toString()).collection("users").document(currentUser.getUid()).addSnapshotListener((documentSnapshot, e) -> {
           if(documentSnapshot!=null){
               isRestaurantChosenByUserTodayLiveData.postValue(true);
           }else {
               isRestaurantChosenByUserTodayLiveData.postValue(false);
           }
        });
        return isRestaurantChosenByUserTodayLiveData;
    }

}
