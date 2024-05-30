/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 25/02/24, 4:27 pm
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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.leo.nopasswordforyou.R;
import com.leo.nopasswordforyou.databinding.ActivityLoginPageBinding;
import com.leo.nopasswordforyou.viewmodel.Login_pageVM;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class login_page extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private ActivityLoginPageBinding binding;
    //  private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(login_page.this, ShowPass.class));
            finish();
        }
        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Login_pageVM vm = new ViewModelProvider(this).get(Login_pageVM.class);
        vm.init(this, mAuth);
        AlertDialog alertDialog;
        alertDialog = new
                MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);





        binding.doSignUp.setOnClickListener(v -> {
            binding.loginLayout.setVisibility(View.GONE);
            binding.signUpLayout.setVisibility(View.VISIBLE);
        });
        binding.doSignIn.setOnClickListener(v -> {
            binding.loginLayout.setVisibility(View.VISIBLE);
            binding.signUpLayout.setVisibility(View.GONE);
        });


        // log in

        // log in

        binding.logIn.setOnClickListener(v -> {
            if (binding.emailTextLogIn.getText().toString().trim().isEmpty() || binding.passTextLogIn.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
                //  makeToast(this,"Fields are empty");
            } else {
                if (mAuth.getCurrentUser() != null) mAuth.signOut();
                alertDialog.show();
                vm.loginAccount(binding.emailTextLogIn.getText().toString().trim(),
                        binding.passTextLogIn.getText().toString().trim(),
                        this, s -> {
                            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            if (mAuth.getCurrentUser() != null) {
                                startActivity(new Intent(login_page.this, ShowPass.class));
                                finish();
                            }
                            return null;
                        });
            }
        });

        //sign up


        binding.btnSignUp.setOnClickListener(v -> {
            if (binding.emailTextSignUp.getText().toString().trim().isEmpty()
                    || binding.passTextSignUp.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "fields are empty !", Toast.LENGTH_SHORT).show();

            } else {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                }
                alertDialog.show();
                vm.createAccount(binding.emailTextSignUp.getText().toString(),
                        binding.passTextSignUp.getText().toString(),
                        this,
                        (s) -> {
                    alertDialog.dismiss();
                            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                            if (mAuth.getCurrentUser() != null) {
                                startActivity(new Intent(login_page.this, ShowPass.class));
                                finish();
                            }
                            return null;

                        });


            }
        });
    }


}