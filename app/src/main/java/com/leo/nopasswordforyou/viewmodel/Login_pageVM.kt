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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListDao
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Login_pageVM @Inject constructor(val passesDao: PassesDao, val passListDao: PassListDao) :
    ViewModel() {

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
                    populateDb() {
                        result.invoke("Success")
                    }
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

    private fun populateDb(result: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val dbPassList: CollectionReference =
            db.collection("PasswordManager").document(auth.currentUser!!.uid)
                .collection("YourPass")

        dbPassList.get().addOnSuccessListener {
            val data = ArrayList<PassListEntity>()
            it.documents.forEach { doc ->
                if (doc != null) {
                    doc.toObject<PassListEntity>()?.let { it1 -> data.add(it1) }
                }
            }
            viewModelScope.launch {
                passListDao.insertAllNewPass(data)
            }
        }

        val dbPass =
            db.collection("Passwords")
                .document(auth.currentUser!!.uid)
                .collection("YourPass")

        dbPass.get().addOnSuccessListener {
            val data = ArrayList<PassesEntity>()
            it.documents.forEach { doc ->
                doc.toObject<PassesEntity>()?.let { it1 -> data.add(it1) }
            }
            viewModelScope.launch {
                passesDao.insertAllPass(data)
                result.invoke()
            }

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