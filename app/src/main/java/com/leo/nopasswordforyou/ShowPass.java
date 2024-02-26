package com.leo.nopasswordforyou;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
            String ToDecode = (String) documentSnapshot.get("pass");
            String UserId = (String) documentSnapshot.get("UserId");
            String decodedData = "No Key Added";
            try {
                Security security = new Security(this);
                decodedData = security.decryptData(ToDecode);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException |
                     CertificateException | IOException | InvalidAlgorithmParameterException |
                     IllegalBlockSizeException | UnrecoverableEntryException | BadPaddingException |
                     NoSuchProviderException | InvalidKeyException | InvalidKeySpecException e) {
                Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("tag", decodedData);
            AlertDialog alertDialog1;

            alertDialog1 = new MaterialAlertDialogBuilder(this).setView(R.layout.custom_show_pass).create();
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.setCancelable(true);
            alertDialog1.show();

            MaterialTextView passShowCustom = alertDialog1.findViewById(R.id.passShowCustom);
            MaterialTextView passUserIdShowCustom = alertDialog1.findViewById(R.id.passUserIdShowCustom);
            FloatingActionButton showPassEyeCustom = alertDialog1.findViewById(R.id.showPassEyeCustom);
            MaterialButton copyPassCustom = alertDialog1.findViewById(R.id.copyPassCustom);
            MaterialButton copyUserIdCustom = alertDialog1.findViewById(R.id.copyUserIdCustom);
            if (passShowCustom != null) {
                passShowCustom.setText(decodedData);

                if (showPassEyeCustom != null) {
                    final boolean[] a = {true};
                    showPassEyeCustom.setOnClickListener(v1 -> {
                        if (a[0]) {
                            passShowCustom.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            a[0] = false;
                        } else {
                            a[0] = true;
                            passShowCustom.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    });
                }
            }

            if (copyUserIdCustom != null) {
                copyUserIdCustom.setOnClickListener(v12 -> {
                    copy(UserId);
                });
            }
            if (copyPassCustom != null) {
                String finalDecodedData = decodedData;
                copyPassCustom.setOnClickListener(v13 -> {
                    copy(finalDecodedData);
                });
            }
            if (passUserIdShowCustom != null) {
                passUserIdShowCustom.setText(UserId);

            }
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
