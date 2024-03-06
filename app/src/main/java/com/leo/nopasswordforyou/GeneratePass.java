/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 29/02/24, 7:44 pm
 *  Copyright (c) 2024 . All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.leo.nopasswordforyou;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leo.nopasswordforyou.helper.NewPass;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class GeneratePass extends AppCompatActivity {

    AppCompatSpinner spinnerNumbers, spinnerSpecialSym, spinnerSmallLetter, spinnerCapLetter;
    TextView passText, textView;
    Button copyPassEnc;

    FloatingActionButton copyPass, regeneratePass, saveToCloud;
    MaterialSwitch customSetSwitch;
    LinearLayout customSettings;
    ConstraintLayout layout2;

    byte alphaCapLength = 4;
    byte specialSymbol = 2;
    byte numberslen = 4;
    byte alphaSmallLength = 6;
    byte passLength = 16;
    Security security = null;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pass);


        NewPass newPass = new NewPass();
        layout2 = findViewById(R.id.layout2);
        customSettings = findViewById(R.id.customSettings);

        copyPass = findViewById(R.id.copyPass);
        customSetSwitch = findViewById(R.id.customSetSwitch);

        spinnerCapLetter = findViewById(R.id.spinnerCapLetter);
        spinnerSmallLetter = findViewById(R.id.spinnerSmallLetter);
        spinnerSpecialSym = findViewById(R.id.spinnerSpecialSym);
        spinnerNumbers = findViewById(R.id.spinnerNumbers);

        copyPassEnc = findViewById(R.id.copyPassEnc);


        regeneratePass = findViewById(R.id.regeneratePass);
        saveToCloud = findViewById(R.id.saveToCloud);

        passText = findViewById(R.id.passText);
        passText.setText(newPass.generateNewPass(alphaCapLength, specialSymbol, numberslen, alphaSmallLength, passLength));

        textView = findViewById(R.id.textView);

//TODO make an app for the ads on billboard etc...

        try {
            if (auth.getCurrentUser() == null) {
                security = new Security(this, "NoPassWordForTheNewUser");
            } else
                security = new Security(this, "NOPASSWORDFF!!!!" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        } catch (NoSuchPaddingException |
                 NoSuchAlgorithmException |
                 KeyStoreException |
                 CertificateException |
                 IOException e) {
            Toast.makeText(this, "Something Went Wrong : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        copyPassEnc.setOnClickListener(v -> {
            String copy = "Task Failed";
            try {
                copy = security.encryptData(passText.getText().toString());
            } catch (InvalidAlgorithmParameterException | KeyStoreException |
                     NoSuchAlgorithmException | NoSuchProviderException |
                     InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException |
                     InvalidKeySpecException | UnrecoverableEntryException e) {
                Snackbar.make(v, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                return;
            }

            ClipboardManager clipboard = (ClipboardManager) GeneratePass.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", copy);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(v, "This button is for this release only \n it will be removed in beta ++ releases", 3000);
            Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show();
            finish();
        });
        regeneratePass.setOnClickListener(v -> {
            passText.setText(newPass.generateNewPass(alphaCapLength, specialSymbol, numberslen, alphaSmallLength, passLength));
      /*      try {
                test.setText(security.decryptData(test.getText().toString()));
            } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                     IllegalBlockSizeException | BadPaddingException |
                     NoSuchAlgorithmException | KeyStoreException | NoSuchProviderException |
                     UnrecoverableEntryException | InvalidKeySpecException e) {
                Snackbar.make(v, Objects.requireNonNull(e.getMessage()), 3000).show();
            }*/
        });

        saveToCloud.setOnClickListener(v -> {

            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Login First !", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog alertDialog;
            alertDialog = new MaterialAlertDialogBuilder(this).setView(R.layout.custom_save_to_cloud).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);


            alertDialog.show();
            AppCompatEditText passTitleCustom, passUserIdCustom, passDescCustom, passSaveCustom;
            FloatingActionButton passDoneCustom, exitButtonCustom;
            MaterialButton newPassCustom;

            newPassCustom = alertDialog.findViewById(R.id.newPassCustom);
            passSaveCustom = alertDialog.findViewById(R.id.passSaveCustom);

            if (passSaveCustom != null) {
                passSaveCustom.setVisibility(View.GONE);
            }
            if (newPassCustom != null) {
                newPassCustom.setVisibility(View.GONE);
            }

            passTitleCustom = alertDialog.findViewById(R.id.passTitleCustom);
            passUserIdCustom = alertDialog.findViewById(R.id.passUserIdCustom);
            passDescCustom = alertDialog.findViewById(R.id.passDescCustom);
            passDoneCustom = alertDialog.findViewById(R.id.passDoneCustom);
            exitButtonCustom = alertDialog.findViewById(R.id.exitButtonCustom);

            if (exitButtonCustom != null) {
                exitButtonCustom.setOnClickListener(v12 -> alertDialog.dismiss());
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }


            if (passDoneCustom != null) {

                passDoneCustom.setOnClickListener(v1 -> {
                    if ((Objects.requireNonNull(Objects.requireNonNull(passTitleCustom).getText())).toString().isEmpty()) {
                        Snackbar.make(v1, "Empty title", 2000).show();
                    } else {
                        AlertDialog alertDialog1;
                        alertDialog1 = new MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2).create();
                        alertDialog1.setCanceledOnTouchOutside(false);
                        alertDialog1.setCancelable(false);
                        alertDialog1.show();
                        TextView t = alertDialog1.findViewById(R.id.loadingText);
                        if (t != null) {
                            t.setText("secure uploading");
                        }
                        //   Snackbar.make(vie, "Uploading", 2000).show();
                        String passTitle = passTitleCustom.getText().toString();
                        String passDesc = "empty";
                        String passUserId = "empty";
                        if (passUserIdCustom != null) {
                            passUserId = Objects.requireNonNull(passUserIdCustom.getText()).toString();
                        }
                        if (passDescCustom != null) {
                            passDesc = Objects.requireNonNull(passDescCustom.getText()).toString();
                        }

                        alertDialog.dismiss();
                        String encPass = "";
                        try {
                            encPass = security.encryptData(passText.getText().toString());
                        } catch (InvalidAlgorithmParameterException | KeyStoreException |
                                 NoSuchAlgorithmException | NoSuchProviderException |
                                 InvalidKeyException |
                                 IllegalBlockSizeException | BadPaddingException |
                                 InvalidKeySpecException | UnrecoverableEntryException e) {
                            Snackbar.make(v, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                            alertDialog1.dismiss();
                            return;
                        }

                        if (encPass.equals("")) {
                            Toast.makeText(this, "Error : Contact Support", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        if (auth.getCurrentUser() != null) {
                            String id = String.valueOf(System.currentTimeMillis());
                            DocumentReference dbPass =
                                    db.collection("PasswordManager")
                                            .document(auth.getCurrentUser().getUid())
                                            .collection("YourPass").document(id + passTitle);

                            Map<String, String> data = new HashMap<>();
                            data.put("Title", passTitle);
                            data.put("Desc", passDesc);
                            data.put("id", id + passTitle);
                            String finalEncPass = encPass;
                            String finalPassUserId = passUserId;
                            dbPass.set(data).addOnSuccessListener(documentReference -> {
                                data.clear();
                                data.put("pass", finalEncPass);
                                data.put("UserId", finalPassUserId);
                                db.collection("Passwords")
                                        .document(auth.getCurrentUser().getUid())
                                        .collection("YourPass").document(id + passTitle).set(data).addOnSuccessListener(unused -> {
                                            Toast.makeText(GeneratePass.this, "Successfully completed", Toast.LENGTH_SHORT).show();
                                            alertDialog1.dismiss();
                                        }).addOnFailureListener(e -> {
                                            alertDialog1.dismiss();
                                            Toast.makeText(GeneratePass.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(GeneratePass.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                alertDialog1.dismiss();
                            });


                        } else {
                            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
                            alertDialog1.dismiss();
                        }


                    }
                });
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }


        });

        copyPass.setOnClickListener(v -> {


            ClipboardManager clipboard = (ClipboardManager) GeneratePass.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", passText.getText());
            clipboard.setPrimaryClip(clip);
            Handler handler = new Handler();
            copyPass.setImageResource(R.drawable.icon_done_24);
            //    copyPass.setBackgroundResource(R.drawable.icon_done_24);
            handler.postDelayed(() -> copyPass.setImageResource(R.drawable.icon_copy_24), 1500);


        });


        String[] values = {"10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                , values);


        spinnerCapLetter.setAdapter(adapter);
        spinnerNumbers.setAdapter(adapter);
        spinnerSmallLetter.setAdapter(adapter);
        spinnerSpecialSym.setAdapter(adapter);

        spinnerNumbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberslen = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numberslen = 4;
                updateTotalText();
            }
        });
        spinnerCapLetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)     {
                alphaCapLength = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                alphaCapLength = 4;
                updateTotalText();
            }
        });
        spinnerSmallLetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alphaSmallLength = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numberslen = 6;
                updateTotalText();
            }
        });
        spinnerSpecialSym.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialSymbol = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numberslen = 2;
                updateTotalText();
            }
        });
        customSetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                customSettings.setVisibility(View.VISIBLE);
                numberslen = Byte.parseByte((String) spinnerNumbers.getSelectedItem());
                alphaCapLength = Byte.parseByte((String) spinnerCapLetter.getSelectedItem());
                alphaSmallLength = Byte.parseByte((String) spinnerSmallLetter.getSelectedItem());
                specialSymbol = Byte.parseByte((String) spinnerSpecialSym.getSelectedItem());
            } else {
                customSettings.setVisibility(View.GONE);
                alphaCapLength = 4;
                specialSymbol = 2;
                numberslen = 4;
                alphaSmallLength = 6;
                passLength = 16;
            }
        });


    }

    private void updateTotalText() {
        int total = alphaCapLength + alphaSmallLength + specialSymbol + numberslen;
        String k = String.valueOf(total);
        textView.setText(k);
    }


}