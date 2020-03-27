package com.sbizzera.go4lunch.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreUser;
import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse.DetailResult;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;


public class FireStoreService {

    private static final String TAG = "FireStoreService";
    private String currentUserId = FirebaseAuthService.getUser().getUid();
    private FirebaseUser currentUser = FirebaseAuthService.getUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    private CollectionReference restaurants = db.collection("restaurants");
    private CollectionReference dates = db.collection("dates");


    //Insert or Update user in FiresStore
    public void updateUserInDb() {
        String photoUrl = null;
        if (currentUser.getPhotoUrl() != null) {
            photoUrl = currentUser.getPhotoUrl().toString();
        }

        users.document(currentUserId).set(new FireStoreUser(
                currentUserId,
                currentUser.getDisplayName(),
                photoUrl
        ));
    }


    public void updateRestaurantLike(DetailResult restaurant) {
        restaurants.document(restaurant.getPlaceId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.getData() != null) {
                //update like
                FireStoreRestaurant fetchedRestaurant = documentSnapshot.toObject(FireStoreRestaurant.class);
                if (fetchedRestaurant != null ) {
                    List<String>likes = new ArrayList<>();
                    if (fetchedRestaurant.getLikesIds()!=null){
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

    public void updateRestaurantChoice(DetailResult restaurant) {
        FireStoreLunch lunchToAdd = new FireStoreLunch(
                currentUserId,
                currentUser.getDisplayName(),
                currentUser.getPhotoUrl().toString(),
                restaurant.getPlaceId(),
                restaurant.getName()
        );
        Log.d(TAG, "updateRestaurantChoice: "+ restaurant.getName() + restaurant.getPlaceId());
        restaurants.document(restaurant.getPlaceId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.getData() != null) {
                dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).get().addOnSuccessListener(documentSnapshot2 -> {
                    if (documentSnapshot2 != null && documentSnapshot2.getData() != null) {
                        //user has already made a choice
                        FireStoreLunch existingLunch = documentSnapshot2.toObject(FireStoreLunch.class);
                        // if choice is same restaurant remove document and update count
                        if (existingLunch != null && existingLunch.getRestaurantId().equals(restaurant.getPlaceId())) {
                            dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).delete();
                            // if not same restaurant, update lunch
                        } else {
                            dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).update("restaurantId", restaurant.getPlaceId(),"restaurantName",restaurant.getName());
//                            dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).update("restaurantName", restaurant.getName());
                        }
                    } else {
                        //user hasn't made a choice
                        //create lunch
                        dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).set(lunchToAdd);

                    }
                });
            } else {
                //create restaurant add lunch and update count
                FireStoreRestaurant restaurantToAdd = new FireStoreRestaurant(
                        restaurant.getPlaceId(),
                        restaurant.getName(),
                        restaurant.getGeometry().getLocation().getLat(),
                        restaurant.getGeometry().getLocation().getLng()
                );
                restaurants.document(restaurant.getPlaceId()).set(restaurantToAdd);
                dates.document(LocalDate.now().toString()).collection("lunches").document(currentUserId).set(lunchToAdd);
            }
        });
    }


    public LiveData<Boolean> isRestaurantLikedByUser(String id) {
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
            if (documentSnapshot != null) {
                FireStoreRestaurant fetchedRestaurant = documentSnapshot.toObject(FireStoreRestaurant.class);
                if (fetchedRestaurant != null ) {
                    int likesCount = 0;
                    if (fetchedRestaurant.getLikesIds()!=null){
                        likesCount = fetchedRestaurant.getLikesIds().size();
                    }
                    likesCountLiveData.postValue(likesCount);
                } else {
                    likesCountLiveData.postValue(0);
                }
            } else {
                likesCountLiveData.postValue(0);
            }
        });
        return likesCountLiveData;
    }

    public LiveData<Boolean> isRestaurantChosenByUserToday(String id) {
        MutableLiveData<Boolean> isRestaurantChosenByUserTodayLiveData = new MutableLiveData<>();
        dates.document(LocalDate.now().toString()).collection("lunches")
                .document(currentUserId)
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (documentSnapshots!=null && documentSnapshots.exists() && documentSnapshots.getData()!=null &&documentSnapshots.getData().containsValue(id)){
                        isRestaurantChosenByUserTodayLiveData.postValue(true);
                    }
                    else{
                        isRestaurantChosenByUserTodayLiveData.postValue(false);
                    }
                });

        return isRestaurantChosenByUserTodayLiveData;
    }

    public LiveData<List<FireStoreUser>> getTodayListOfUsers(String id) {
        MutableLiveData<List<FireStoreUser>> listUsersLiveData = new MutableLiveData<>();
        List<FireStoreUser> usersToSend = new ArrayList<>();
        dates.document(LocalDate.now().toString()).collection("lunches").whereEqualTo("restaurantId",id).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots!=null) {
                usersToSend.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    FireStoreUser userToAdd = document.toObject(FireStoreUser.class);
                    usersToSend.add(userToAdd);
                }
                listUsersLiveData.postValue(usersToSend);
            }
        });
        return listUsersLiveData;
    }

    public LiveData<List<FireStoreUser>> getAllUsers() {
        MutableLiveData<List<FireStoreUser>> allUsersLiveData = new MutableLiveData<>();
        users.addSnapshotListener((queryDocumentSnapshots,e) -> {
            if (queryDocumentSnapshots!=null ){
                List<FireStoreUser> usersList = new ArrayList<>();
                for (int i = 0; i <queryDocumentSnapshots.getDocuments().size() ; i++) {
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
            for (int i = 0; i <queryDocumentSnapshots.getDocuments().size() ; i++) {
                FireStoreLunch lunch = queryDocumentSnapshots.getDocuments().get(i).toObject(FireStoreLunch.class);
                lunchList.add(lunch);
            }
            allTodaysLunchesLiveData.postValue(lunchList);
        });
        return allTodaysLunchesLiveData;
    }
}
