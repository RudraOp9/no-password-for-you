package com.leo.nopasswordforyou;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.leo.nopasswordforyou.helper.ItemClickListner;
import com.leo.nopasswordforyou.helper.PassAdapter;
import com.leo.nopasswordforyou.helper.PassAdapterData;
import com.leo.nopasswordforyou.helper.Security;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ShowPass extends AppCompatActivity implements ItemClickListner {
    RecyclerView rvPasses;
    FirebaseFirestore db;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference dbTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pass);
        rvPasses = findViewById(R.id.rvPasses);

        ArrayList<PassAdapterData> passData = new ArrayList<>();
        passData.add(new PassAdapterData("Title", "Desc", "id"));

      /*  passData.add(new PassAdapterData("Youtube","my youtube password","52"));
        passData.add(new PassAdapterData("Instagram","my Instagram password","2"));
        passData.add(new PassAdapterData("Facebook","my facebook password","5"));
        passData.add(new PassAdapterData("github","my github password","6"));
        passData.add(new PassAdapterData("whatsapp","my whatsapp password","12"));
        passData.add(new PassAdapterData("test","my test password","63"));
        passData.add(new PassAdapterData("hard time","my  password","1"));
        passData.add(new PassAdapterData("password","my password","0"));*/


        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    auth.removeAuthStateListener(this);
                    finish();

                }
            }
        });

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, login_page.class));
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        dbTitles = db.collection("PasswordManager").document(auth.getCurrentUser().getUid()).collection("YourPass");
        PassAdapter passAdapter = new PassAdapter(passData, auth.getCurrentUser().getUid(), db);
        passAdapter.setClickListener(this);
        rvPasses.setAdapter(passAdapter);
        rvPasses.setLayoutManager(new LinearLayoutManager(this));
        dbTitles.get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (queryDocumentSnapshots != null) {
                for (DocumentSnapshot a : queryDocumentSnapshots.getDocuments()) {
                    passData.add(new PassAdapterData((String) a.get("Title"), (String) a.get("Desc"), (String) a.get("id")));
                }
                passAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(e -> Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_SHORT).show());



    }

    @Override
    public void onClick(View v, String id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, login_page.class));
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
            finish();
        }
        DocumentReference dbPass =
                db.collection("Passwords")
                        .document(auth.getCurrentUser().getUid())
                        .collection("YourPass")
                        .document(id);

        dbPass.get().addOnSuccessListener(documentSnapshot -> {
            String ToDecode = (String )documentSnapshot.get("pass");
            String decodedData = "No Key Added";
            try {
                Security security = new Security(this);
                decodedData = security.decryptData(ToDecode);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException |
                     CertificateException | IOException | InvalidAlgorithmParameterException |
                     IllegalBlockSizeException | UnrecoverableEntryException | BadPaddingException |
                     NoSuchProviderException | InvalidKeyException | InvalidKeySpecException e) {
                Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Log.d("tag", decodedData);
            AlertDialog alertDialog1;
            String finalDecodedData = decodedData;

            alertDialog1 = new MaterialAlertDialogBuilder(this).setView(R.layout.custom_show_pass).create();
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.setCancelable(false);
            alertDialog1.show();

            MaterialTextView passShowCustom = alertDialog1.findViewById(R.id.passShowCustom);

          /*  alertDialog.setButton(0, "Copy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    copy(finalDecodedData);
                    alertDialog.dismiss();
                }
            });*/
            /*alertDialog.setButton(1, "Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });*/
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.setCancelable(false);
            alertDialog1.show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    public void copy(String text){
        ClipboardManager clipboard = (ClipboardManager) ShowPass.this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text",text);
        clipboard.setPrimaryClip(clip);
    }
}
