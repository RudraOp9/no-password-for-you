package com.leo.nopasswordforyou.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataBase {
    private void upload(String id, FirebaseFirestore db, FirebaseAuth auth, String encData) {
        DocumentReference dbPass =
                db.collection("Passwords")
                        .document(auth.getCurrentUser().getUid())
                        .collection("YourPass")
                        .document(id);


    }
}
