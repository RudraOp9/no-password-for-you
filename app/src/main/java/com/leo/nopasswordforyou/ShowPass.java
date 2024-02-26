package com.leo.nopasswordforyou;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ShowPass extends AppCompatActivity implements ItemClickListner {
    RecyclerView rvPasses;
    FirebaseFirestore db;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference dbTitles;
    DocumentReference dbPass;

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
    public void onClick(View v, String id, String Title, String Desc) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, login_page.class));
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
            finish();
        }
        dbPass =
                db.collection("Passwords")
                        .document(auth.getCurrentUser().getUid())
                        .collection("YourPass")
                        .document(id);

        dbPass.get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot == null) {
                Snackbar.make(v, "something went wrong", 2000).show();
                return;
            }
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
            AppCompatImageView passTool = alertDialog1.findViewById(R.id.passTool);


            // delete of update

            if (passTool != null) {
                String finalDecodedData1 = decodedData;
                passTool.setOnClickListener(v14 -> {

                    AlertDialog.Builder ad = new AlertDialog.Builder(ShowPass.this)
                            .setTitle("Alert !")
                            .setMessage("You are changing your password Fields.")
                            .setCancelable(true)
                            .setPositiveButton("Update", (dialog, which) -> {

                                AlertDialog alertDialog2 = new MaterialAlertDialogBuilder(this).setView(R.layout.custom_save_to_cloud).create();
                                alertDialog2.setCanceledOnTouchOutside(false);
                                alertDialog2.setCancelable(true);
                                alertDialog2.show();


                                alertDialog2.show();
                                AppCompatEditText passTitleCustom, passUserIdCustom, passDescCustom, passSaveCustom;
                                FloatingActionButton passDoneCustom, exitButtonCustom;
                                MaterialButton newPassCustom;
                                passSaveCustom = alertDialog2.findViewById(R.id.passSaveCustom);
                                passTitleCustom = alertDialog2.findViewById(R.id.passTitleCustom);
                                passUserIdCustom = alertDialog2.findViewById(R.id.passUserIdCustom);
                                passDescCustom = alertDialog2.findViewById(R.id.passDescCustom);
                                passDoneCustom = alertDialog2.findViewById(R.id.passDoneCustom);
                                exitButtonCustom = alertDialog2.findViewById(R.id.exitButtonCustom);
                                newPassCustom = alertDialog2.findViewById(R.id.newPassCustom);

                                if (passSaveCustom != null) {
                                    passSaveCustom.setText(finalDecodedData1);
                                }
                                if (passTitleCustom != null) {
                                    passTitleCustom.setText(Title);
                                }
                                if (passDescCustom != null) {
                                    passDescCustom.setText(Desc);
                                }
                                if (passUserIdCustom != null) {
                                    passUserIdCustom.setText(UserId);
                                }
                                if (newPassCustom != null) {
                                    newPassCustom.setOnClickListener(v15 -> {
                                        Toast.makeText(this, "Copy the Encoded PassWord", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ShowPass.this, GeneratePass.class));
                                    });
                                }

                                //: MAKE CHANGES IN SHOW PASS.CLASS FOR NEW PASS CUSTOM.

                                if (exitButtonCustom != null) {
                                    exitButtonCustom.setOnClickListener(v12 -> alertDialog2.dismiss());
                                } else {
                                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    alertDialog2.dismiss();
                                }


                                if (passDoneCustom != null) {


                                    passDoneCustom.setOnClickListener(v16 -> {
                                        assert passSaveCustom != null;
                                        if (Objects.requireNonNull(passSaveCustom.getText()).toString().length() < 50) {
                                            Toast.makeText(this, "Kindly copy the encoded pass only", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
                                        Map<String, String> data = new HashMap<>();
                                        assert passTitleCustom != null;
                                        if (!Objects.requireNonNull(passTitleCustom.getText()).toString().trim().equals(Title)) {
                                            data.put("Title", passTitleCustom.getText().toString());
                                        } else return;

                                        assert passDescCustom != null;
                                        if (!Objects.requireNonNull(passDescCustom.getText()).toString().trim().equals(Desc)) {
                                            data.put("Desc", passDescCustom.getText().toString());
                                        }
                                        if ((data.size() != 0)) {
                                            DocumentReference dbPass2 =
                                                    db.collection("PasswordManager")
                                                            .document(auth.getCurrentUser().getUid())
                                                            .collection("YourPass")
                                                            .document(id + Title);
                                            updatePassCloudSerial(data, dbPass2);
                                        }
                                        data.clear();
                                        if (ToDecode != null && !ToDecode.equals(Objects.requireNonNull(passSaveCustom.getText()).toString())) {
                                            data.put("pass", passSaveCustom.getText().toString());
                                        }
                                        assert passUserIdCustom != null;
                                        if (!Objects.requireNonNull(passUserIdCustom.getText()).toString().equals(UserId)) {
                                            data.put("UserId", passUserIdCustom.getText().toString());
                                        }
                                        if (data.size() != 0) {
                                            updatePassCloudSerial(data, dbPass);
                                        }
                                        alertDialog2.dismiss();
                                        Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();

                                    });
                                }

                                dialog.cancel();

                            })
                            .setNegativeButton("Delete", (dialog, which) -> {
                                DocumentReference dbPass2 =
                                        db.collection("PasswordManager")
                                                .document(auth.getCurrentUser().getUid())
                                                .collection("YourPass")
                                                .document(id + Title);

                                dbPass2.delete();
                                dbPass.delete();

                                dialog.cancel();
                            });

                    ad.create().show();


                });
            }

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

    private void updatePassCloudSerial(Map<String, String> data, DocumentReference dbPass2) {
        dbPass2.set(data, SetOptions.merge()).addOnFailureListener(e -> Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) ShowPass.this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }
}
