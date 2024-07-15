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
package com.leo.nopasswordforyou.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.databinding.ActivityLoginPageBinding
import com.leo.nopasswordforyou.viewmodel.Login_pageVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class login_page : AppCompatActivity() {
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    private var binding: ActivityLoginPageBinding? = null


    //  private FirebaseFirestore db = FirebaseFirestore.getInstance();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mAuth.currentUser != null) {
            startActivity(Intent(this@login_page, ShowPass::class.java))
            finish()
        }
        binding = ActivityLoginPageBinding.inflate(
            layoutInflater
        )
        val view: View = binding!!.root
        setContentView(view)
        val vm = ViewModelProvider(this)[Login_pageVM::class.java]
        vm.init(this, mAuth)
        val alertDialog =
            MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)





        binding!!.doSignUp.setOnClickListener {
            binding!!.loginLayout.visibility = View.GONE
            binding!!.signUpLayout.visibility = View.VISIBLE
        }
        binding!!.doSignIn.setOnClickListener {
            binding!!.loginLayout.visibility = View.VISIBLE
            binding!!.signUpLayout.visibility = View.GONE
        }


        // log in

        // log in
        binding!!.logIn.setOnClickListener {
            if (binding!!.emailTextLogIn.text.toString().trim { it <= ' ' }
                    .isEmpty() || binding!!.passTextLogIn.text.toString().trim { it <= ' ' }
                    .isEmpty()) {
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
                //  makeToast(this,"Fields are empty");
            } else {
                if (mAuth.currentUser != null) mAuth.signOut()
                alertDialog.show()
                vm.loginAccount(
                    binding!!.emailTextLogIn.text.toString().trim { it <= ' ' },
                    binding!!.passTextLogIn.text.toString().trim { it <= ' ' },
                    this
                ) { s: String? ->
                    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    if (mAuth.currentUser != null) {
                        startActivity(Intent(this@login_page, ShowPass::class.java))
                        finish()
                    }
                }
            }
        }


        //sign up
        binding!!.btnSignUp.setOnClickListener {
            if (binding!!.emailTextSignUp.text.toString().trim { it <= ' ' }
                    .isEmpty()
                || binding!!.passTextSignUp.text.toString().trim { it <= ' ' }.isEmpty()
            ) {
                Toast.makeText(this, "fields are empty !", Toast.LENGTH_SHORT).show()
            } else {
                if (mAuth.currentUser != null) {
                    mAuth.signOut()
                }
                alertDialog.show()
                vm.createAccount(
                    binding!!.emailTextSignUp.text.toString(),
                    binding!!.passTextSignUp.text.toString(),
                    this
                ) { s: String? ->
                    alertDialog.dismiss()
                    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
                    if (mAuth.currentUser != null) {
                        startActivity(Intent(this@login_page, ShowPass::class.java))
                        finish()
                    }

                }
            }
        }
    }
}