/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 10/03/24, 7:12 pm
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

package com.leo.nopasswordforyou.activities;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leo.nopasswordforyou.R;
import com.leo.nopasswordforyou.databinding.ActivityGeneratePassBinding;
import com.leo.nopasswordforyou.secuirity.Security;
import com.leo.nopasswordforyou.viewmodel.GeneratePassVM;

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

    Security security = null;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    GeneratePassVM vm;
    private ActivityGeneratePassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGeneratePassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        vm = new ViewModelProvider(this).get(GeneratePassVM.class);

        vm.getPassWord().observe(this, s -> {
            binding.passText.setText(s);
        });

        vm.getTotal().observe(this, s -> {
            binding.total.setText(s);
        });




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

        binding.copyPassEnc.setOnClickListener(v -> {
            String copy = "Task Failed";
            try {
                if (binding.passText.getText() != null)
                    copy = security.encryptData(Objects.requireNonNull(binding.passText.getText()).toString());
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
            //    Snackbar.make(v, "This button is for this release only \n it will be removed in beta ++ releases", 3000);
            Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show();
            finish();
        });
        binding.regeneratePass.setOnClickListener(v -> {
            vm.genNewPass();
        });

        binding.saveToCloud.setOnClickListener(v -> {

            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Login First !", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setView(R.layout.custom_save_to_cloud).create();
            alertDialog.setCanceledOnTouchOutside(false);
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
                            if (binding.passText.getText() != null)
                                encPass = security.encryptData(binding.passText.getText().toString());
                        } catch (InvalidAlgorithmParameterException | KeyStoreException |
                                 NoSuchAlgorithmException | NoSuchProviderException |
                                 InvalidKeyException |
                                 IllegalBlockSizeException | BadPaddingException |
                                 InvalidKeySpecException | UnrecoverableEntryException e) {
                            Snackbar.make(v, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                            alertDialog1.dismiss();
                            return;
                        }

                        if (encPass.isEmpty()) {
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

        binding.copyPass.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) GeneratePass.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", binding.passText.getText());
            clipboard.setPrimaryClip(clip);
            Handler handler = new Handler();
            binding.copyPass.setImageResource(R.drawable.icon_done_24);
            //    copyPass.setBackgroundResource(R.drawable.icon_done_24);
            handler.postDelayed(() -> binding.copyPass.setImageResource(R.drawable.icon_copy_24), 1500);
        });


        String[] values = {"10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                , values);


        binding.spinnerCapLetter.setAdapter(adapter);
        binding.spinnerNumbers.setAdapter(adapter);
        binding.spinnerSmallLetter.setAdapter(adapter);
        binding.spinnerSpecialSym.setAdapter(adapter);
        binding.spinnerCapLetter.setSelection(4);
        binding.spinnerNumbers.setSelection(4);
        binding.spinnerSmallLetter.setSelection(6);
        binding.spinnerSpecialSym.setSelection(2);


        binding.spinnerNumbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.setNumberslen(Byte.parseByte(values[position]));

                vm.updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setNumberslen((byte) 4);
                vm.updateTotalText();
            }
        });
        binding.spinnerCapLetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)     {
                vm.setAlphaCapLength(Byte.parseByte(values[position]));
                vm.updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setAlphaCapLength((byte) 4);
                vm.updateTotalText();
            }
        });
        binding.spinnerSmallLetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.setAlphaSmallLength(Byte.parseByte(values[position]));
                vm.updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setNumberslen((byte) 6);
                vm.updateTotalText();
            }
        });
        binding.spinnerSpecialSym.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.setSpecialSymbol(Byte.parseByte(values[position]));
                vm.updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setNumberslen((byte) 2);
                ;
                vm.updateTotalText();
            }
        });
        binding.customSetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.customSettings.setVisibility(View.VISIBLE);
                vm.setNumberslen(Byte.parseByte((String) binding.spinnerNumbers.getSelectedItem()));
                vm.setAlphaCapLength(Byte.parseByte((String) binding.spinnerCapLetter.getSelectedItem()));
                vm.setAlphaSmallLength(Byte.parseByte((String) binding.spinnerSmallLetter.getSelectedItem()));
                vm.setSpecialSymbol(Byte.parseByte((String) binding.spinnerSpecialSym.getSelectedItem()));
            } else {
                binding.customSettings.setVisibility(View.GONE);
                vm.setNumberslen((byte) 4);
                vm.setAlphaCapLength((byte) 4);
                vm.setAlphaSmallLength((byte) 6);
                vm.setSpecialSymbol((byte) 2);
                vm.setPassLength((byte) 16);
            }
        });
    }

}