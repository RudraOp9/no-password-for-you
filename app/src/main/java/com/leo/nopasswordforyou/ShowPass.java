package com.leo.nopasswordforyou;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
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
import java.util.Arrays;
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
    FloatingActionButton syncPass;
    ArrayList<PassAdapterData> passData;
    PassAdapter passAdapter;
    AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pass);


        alertDialog1 = new MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2).create();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.setCancelable(false);
        alertDialog1.show();
        TextView t = alertDialog1.findViewById(R.id.loadingText);
        if (t != null) {
            t.setText("Getting passwords");
        }

        rvPasses = findViewById(R.id.rvPasses);
        syncPass = findViewById(R.id.syncPass);
        passData = new ArrayList<>();


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
        passAdapter = new PassAdapter(passData, auth.getCurrentUser().getUid(), db);
        passAdapter.setClickListener(this);
        rvPasses.setAdapter(passAdapter);
        rvPasses.setLayoutManager(new LinearLayoutManager(this));
        getPasses(Source.CACHE);


        syncPass.setOnClickListener(v -> getPasses(Source.SERVER));


    }

    private void getPasses(Source source) {
        if (!alertDialog1.isShowing()) {
            alertDialog1.show();
        }

        passData.clear();
        dbTitles.get(source).addOnSuccessListener(queryDocumentSnapshots -> {

            if (queryDocumentSnapshots != null) {
                for (DocumentSnapshot a : queryDocumentSnapshots.getDocuments()) {
                    passData.add(new PassAdapterData((String) a.get("Title"), (String) a.get("Desc"), (String) a.get("id")));
                }
                alertDialog1.dismiss();
                passAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            alertDialog1.dismiss();
        });

    }

    @Override
    public void onClick(View v, String id, String Title, String Desc) {
//        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, login_page.class));
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
            finish();
        }
        DocumentReference dbPass3 = dbTitles.document(id);
        dbPass =
                db.collection("Passwords")
                        .document(auth.getCurrentUser().getUid())
                        .collection("YourPass")
                        .document(id);

        dbPass.get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.getData() == null) {
                Snackbar.make(v, "something went wrong", 2000).show();
            } else {

                String ToDecode = (String) documentSnapshot.get("pass");
                String UserId = (String) documentSnapshot.get("UserId");
                String decodedData = "No Key Added";
                try {
                    Security security = new Security(this, "NOPASSWORDFF!!!!" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    decodedData = security.decryptData(ToDecode);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException |
                         CertificateException | IOException | InvalidAlgorithmParameterException |
                         IllegalBlockSizeException | UnrecoverableEntryException |
                         BadPaddingException |
                         NoSuchProviderException | InvalidKeyException |
                         InvalidKeySpecException e) {
                    Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("tag", Objects.requireNonNull(e.getCause()) + " and " + Arrays.toString(e.getStackTrace()));
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
                    //    String finalDecodedData1 = decodedData;
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

                                    if (!(passSaveCustom != null
                                            && passUserIdCustom != null
                                            && passTitleCustom != null
                                            && passDescCustom != null
                                            && newPassCustom != null
                                            && exitButtonCustom != null
                                            && passDoneCustom != null)) {
                                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        alertDialog2.dismiss();
                                        return;
                                    }
                                    passSaveCustom.setText(ToDecode);
                                    passTitleCustom.setText(Title);
                                    passDescCustom.setText(Desc);
                                    passUserIdCustom.setText(UserId);
                                    newPassCustom.setOnClickListener(v15 -> {
                                        Toast.makeText(this, "Copy the Encoded PassWord", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ShowPass.this, GeneratePass.class));
                                    });


                                    //: MAKE CHANGES IN SHOW PASS.CLASS FOR NEW PASS CUSTOM.
                                    exitButtonCustom.setOnClickListener(v12 -> alertDialog2.dismiss());

                                    // pass done custom
                                    passDoneCustom.setOnClickListener(v16 -> {
                                            DocumentReference dbPass2 =
                                                    db.collection("PasswordManager")
                                                            .document(auth.getCurrentUser().getUid())
                                                            .collection("YourPass")
                                                            .document(id);
                                            assert passSaveCustom != null;

                                            Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
                                            Map<String, String> data = new HashMap<>();
                                            assert passTitleCustom != null;
                                            if (!Objects.requireNonNull(passTitleCustom.getText()).toString().trim().equals(Title)) {
                                                data.put("Title", passTitleCustom.getText().toString());
                                            }


                                            assert passDescCustom != null;
                                            if (!Objects.requireNonNull(passDescCustom.getText()).toString().trim().equals(Desc)) {
                                                data.put("Desc", passDescCustom.getText().toString());
                                            }
                                            if ((data.size() != 0)) {

                                                dbPass2.set(data, SetOptions.merge()).addOnFailureListener(e -> Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                            }
                                            data.clear();

                                            if (ToDecode != null && !ToDecode.equals(Objects.requireNonNull(passSaveCustom.getText()).toString()) && Objects.requireNonNull(passSaveCustom.getText()).toString().length() > 50) {
                                                data.put("pass", passSaveCustom.getText().toString());
                                            }
                                            assert passUserIdCustom != null;
                                            if (!Objects.requireNonNull(passUserIdCustom.getText()).toString().equals(UserId)) {
                                                data.put("UserId", passUserIdCustom.getText().toString());
                                            }
                                            if (data.size() != 0) {
                                                dbPass2.set(data, SetOptions.merge()).addOnFailureListener(e -> Toast.makeText(ShowPass.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                            }
                                            alertDialog2.dismiss();
                                            Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
                                        });


                                    dialog.cancel();
                                    alertDialog1.dismiss();
                                    //getPasses(Source.CACHE);


                                })
                                .setNegativeButton("Delete", (dialog, which) -> {

                                    Log.d("tag", "id : " + id + " Title : " + Title);
                                    dbPass.delete();
                                    dbPass3.delete().addOnSuccessListener(unused -> Log.d("tag", " delete it")).addOnFailureListener(e -> {
                                        Log.d("tag", "can't delete it");
                                        Toast.makeText(ShowPass.this, e.getMessage() + " Localized : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    });

                                    alertDialog1.dismiss();
                                    getPasses(Source.SERVER);

                                    dialog.dismiss();
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
                    copyUserIdCustom.setOnClickListener(v12 -> copy(UserId));
                }
                if (copyPassCustom != null) {
                    String finalDecodedData = decodedData;
                    copyPassCustom.setOnClickListener(v13 -> copy(finalDecodedData));
                }
                if (passUserIdShowCustom != null) {
                    passUserIdShowCustom.setText(UserId);

                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    public void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) ShowPass.this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }
}
