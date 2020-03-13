package com.sbizzera.go4lunch;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireStoreService {

    private static final String TAG = "FireStoreService";

    public static void addTestToFireBase(String name, String photoUrl){
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Map<String,Object> userToAdd = new HashMap<>();
    userToAdd.put("name",name);
    userToAdd.put("photoUrl",photoUrl);

        db.collection("users").add(userToAdd)
                .addOnSuccessListener(documentReference -> {
            Log.d(TAG, "addTestToFireBase: "+documentReference.getId());
        }).addOnFailureListener(e->{
            Log.d(TAG, "addTestToFireBase: "+ e.getMessage());
        });
    }
}
