/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 20/05/24, 10:02 pm
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

package com.leo.nopasswordforyou.viewmodel

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.leo.nopasswordforyou.R

class Login_pageVM : ViewModel() {

    lateinit var mAuth: FirebaseAuth
    fun init(context: Context, mAuth: FirebaseAuth) {
        this.mAuth = mAuth

    }


    fun loginAccount(email: String, password: String, context: Context, result: (String) -> Unit) {

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                context as Activity
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    result.invoke("Success")

                    //   loggedIn();
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                }
            }.addOnFailureListener { e: Exception ->
                val error = e.message
                if (error != null) {
                    result.invoke(error)
                } else result.invoke("Something went wrong")

            }
    }


    fun createAccount(email: String, password: String, context: Context, result: (String) -> Unit) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.invoke("Success")
                Log.d(ContentValues.TAG, "createUserWithEmail:success")
            }.addOnFailureListener { e: java.lang.Exception ->

                val error = e.message
                if (error != null) {
                    result.invoke(error)
                } else result.invoke("Something went wrong")

            }
    }


}