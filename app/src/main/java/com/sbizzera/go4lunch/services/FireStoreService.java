package com.sbizzera.go4lunch.services;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sbizzera.go4lunch.model.FireStoreRestaurant;
import com.sbizzera.go4lunch.model.FireStoreUser;
import com.sbizzera.go4lunch.model.Test;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;


public class FireStoreService {

    private static final String TAG = "FireStoreService";
    private String currentUserId = FirebaseAuthService.getCurrentUserId();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    private CollectionReference restaurants = db.collection("restaurants");


    public void getCollectionTest() {
        db.collection("restaurants").get().addOnSuccessListener(task -> {
            Log.d(TAG, "getCollectionTest: " + task.getDocuments().get(0).getData().toString());

        });
        db.collection("restaurants").document("ChIJu6K6oKVTAUgRqL_9BqsX2zI").get().addOnSuccessListener(documentSnapshot -> {
            Test test = documentSnapshot.toObject(Test.class);
            Log.d(TAG, "getCollectionTest: " + test.toString());

            Log.d(TAG, "getCollectionTest: " + test.getFrequentation().get(LocalDate.now().toString()));

        });
    }

    public void addRestaurantToFireStore(String restaurantID) {
        restaurants.add(new FireStoreRestaurant(restaurantID,0));
    }

    public void addUserToFireStore(String userId) {
        users.add(new FireStoreUser(userId,null));
    }

    public void checkUserAndInsertInDB() {
        users.whereEqualTo("userId", currentUserId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.getDocuments().size() == 0) {
                addUserToFireStore(currentUserId);
            }
        });
    }

    public void updateRestaurantLikeForUser(String restaurantId){
        restaurants.whereEqualTo("restaurantId",restaurantId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.getDocuments().size()==0){
                addRestaurantToFireStore(restaurantId);
            }
            users.whereEqualTo("userId",currentUserId).get()
                    .addOnSuccessListener(snapshot2 -> {
                        FireStoreUser userToUpdate = snapshot2.getDocuments().get(0).toObject(FireStoreUser.class);
                        List<String> restaurantList = userToUpdate.getLikedRestaurantList();
                        if (restaurantList== null){
                            restaurantList = new ArrayList<>();
                        }
                        if (restaurantList.contains(restaurantId)){
                            restaurantList.remove(restaurantId);
                        }else{
                            restaurantList.add(restaurantId);
                        }
                        userToUpdate.setLikedRestaurantList(restaurantList);
                        users.document(snapshot2.getDocuments().get(0).getId()).set(userToUpdate);
                    });
        });
    }


}
