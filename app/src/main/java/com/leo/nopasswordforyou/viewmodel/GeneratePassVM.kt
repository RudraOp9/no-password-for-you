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

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListDao
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.helper.NewPass
import com.leo.nopasswordforyou.secuirity.Security
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratePassVM @Inject constructor(
    val aliasDao: AliasDao,
    val passesDao: PassesDao,
    val passListDao: PassListDao
) : ViewModel() {
    var alphaCapLength: Byte = 4
    var specialSymbol: Byte = 2
    var numberslen: Byte = 4
    var alphaSmallLength: Byte = 6
    var passLength: Byte = 16
    var newPass: NewPass
    var aliases = ArrayList<String>()
    var selectedAlias = 0


    var passWord: MutableState<String> = mutableStateOf("")
    var total: MutableState<String> = mutableStateOf("")
    var auth: FirebaseAuth = FirebaseAuth.getInstance()


    init {
        passWord.value = ""
        newPass = NewPass();
        getAliases()
        genNewPass()

    }

    fun genNewPass() {
        passWord.value = newPass.generateNewPass(
            alphaCapLength,
            specialSymbol,
            numberslen,
            alphaSmallLength,
            passLength
        )
    }

    fun updateTotalText() {
        total.value = (alphaCapLength + alphaSmallLength + specialSymbol + numberslen).toString()
    }

    fun getAliases() {
        viewModelScope.launch {
            val alias = ArrayList<String>()
            for (a in aliasDao.getAllUsers()) {
                Log.d("tag", a.alias)
                alias.add(a.alias)
            }
            aliases = alias
        }
    }

    fun putPassList(title: String, description: String, id: String, alias: String, modify: Long) {
        viewModelScope.launch {
            passListDao.insertNewPass(
                PassListEntity(
                    title = title,
                    desc = description,
                    alias = alias,
                    passId = id,
                    lastModify = modify
                )
            )
        }
    }

    fun putPasses(passId: String, userId: String, password: String, alias: String) {
        viewModelScope.launch {
            passesDao.insertNewPass(
                PassesEntity(
                    passId = passId,
                    userId = userId,
                    password = password,
                    alias = alias
                )
            )

        }
    }

    fun isLoggedIn(): Boolean {
        if (auth.currentUser == null) {
            return false
        } else {
            return true
        }
    }

    fun encryptPass(context: Context, password: String, error: (String) -> Unit): String? {
        val security = Security(context)
        return security.encryptData(
            password, aliases[selectedAlias], error
        )
    }


    fun putPass(
        encPass: String,
        passTitle: String,
        passDesc: String,
        passUserId: String,
        context: Context,
        status: (code: Int) -> Unit
    ) {
        val alias = aliases[selectedAlias]
        val db = FirebaseFirestore.getInstance()
        if (auth.currentUser != null) {
            val modify = System.currentTimeMillis()
            val id = modify.toString()
            val dbPass =
                db.collection("PasswordManager")
                    .document(auth.currentUser!!.uid)
                    .collection("YourPass").document(id + passTitle)

            val data2 =
                PassListEntity(id + passTitle, passTitle, passDesc, alias, modify)

            dbPass.set(data2).addOnSuccessListener {
                val passData =
                    PassesEntity(id + passTitle, passUserId, encPass, alias)

                db.collection("Passwords")
                    .document(auth.currentUser!!.uid)
                    .collection("YourPass").document(id + passTitle).set(passData)
                    .addOnSuccessListener {
                        putPassList(
                            passTitle,
                            passDesc,
                            id + passTitle,
                            alias,
                            modify
                        )
                        putPasses(
                            id + passTitle,
                            passUserId,
                            encPass,
                            alias
                        )
                        Toast.makeText(
                            context,
                            "Successfully completed",
                            Toast.LENGTH_SHORT
                        ).show()
                        status.invoke(0)
                    }.addOnFailureListener {
                        status.invoke(1)
                        Toast.makeText(
                            context,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
                status.invoke(1)
            }
        } else {
            Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show()
            status.invoke(1)
        }
    }
}