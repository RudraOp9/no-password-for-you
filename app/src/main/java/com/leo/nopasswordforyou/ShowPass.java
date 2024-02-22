package com.leo.nopasswordforyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;
import com.leo.nopasswordforyou.helper.PassAdapter;
import com.leo.nopasswordforyou.helper.PassAdapterData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ShowPass extends AppCompatActivity {
    RecyclerView rvPasses;
    FirebaseFirestore db;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference dbTitles ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pass);
        rvPasses = findViewById(R.id.rvPasses);

        ArrayList<PassAdapterData> passData = new ArrayList<>();
        /*test.add(new PassAdapterData("Youtube","my youtube password"));
        test.add(new PassAdapterData("Instagram","my Instagram password"));
        test.add(new PassAdapterData("Facebook","my facebook password"));
        test.add(new PassAdapterData("github","my github password"));
        test.add(new PassAdapterData("whatsapp","my whatsapp password"));
        test.add(new PassAdapterData("test","my test password"));
        test.add(new PassAdapterData("hard time","my  password"));
        test.add(new PassAdapterData("password","my password"));*/


        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    auth.removeAuthStateListener(this);
                    finish();
                }
            }
        });

        if (auth.getCurrentUser() == null){
            startActivity(new Intent(this,login_page.class));
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
            finish();
        }

        db = FirebaseFirestore.getInstance();
        dbTitles =  db.collection("PasswordManager").document(auth.getCurrentUser().getUid()).collection("YourPass");

        dbTitles.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots != null){
                    for (DocumentSnapshot a:queryDocumentSnapshots.getDocuments()){
                        passData.add(new PassAdapterData((String) a.get("Title"), (String) a.get("Desc"), (String) a.get("id")));
                    }
                }

            }
        }).addOnFailureListener(e -> Toast.makeText(ShowPass.this,e.getMessage(),Toast.LENGTH_SHORT).show());

        PassAdapter passAdapter = new PassAdapter(passData,auth.getCurrentUser().getUid(),db);
        rvPasses.setAdapter(passAdapter);
        rvPasses.setLayoutManager(new LinearLayoutManager(this));

    }
}