/*
 *  No password for you
 *  Copyright (c) 2024 . All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,either version 3 of the License,or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not,see <http://www.gnu.org/licenses/>.
 */

package com.leo.nopasswordforyou.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.util.FilesIO.importFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportVM @Inject constructor(
) : ViewModel() {
    var mappingText: MutableState<String> = mutableStateOf("Select mapping file")
    var passText: MutableState<String> = mutableStateOf("Select pass file")
    var working: MutableState<Boolean> = mutableStateOf(false)
    var cloudImportDialogue: MutableState<Boolean> = mutableStateOf(false)

    val userInputChannel = Channel<Boolean>()
    var inconsistentError: MutableState<Boolean> = mutableStateOf(false)

    var noPassImport: MutableState<Boolean> = mutableStateOf(false)
    var importMapping = true
    var mappingFile: String = ""
    private var passFile: String = ""
    private lateinit var launcher: ManagedActivityResultLauncher<Intent, ActivityResult>


    fun importMappingOrPass() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*" // Allow all file types (you can be more specific if needed)
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("application/octet-stream", "text/plain")

            ) // Add .ppk MIME type
        }
        launcher.launch(intent)
    }

    @Composable
    fun SelectAndReadFile(message: (String) -> Unit, working: (Boolean) -> Unit) {
        val context = LocalContext.current
        launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            working.invoke(true)
            if (result.resultCode == Activity.RESULT_OK) {
                noPassImport.value = false
                viewModelScope.launch(Dispatchers.IO) {
                    importFile(
                        result = result,
                        message = {
                            message.invoke(it)
                            working.invoke(false)
                        },
                        fileExtension = if (importMapping) "mapping" else "pass",
                        context = context,
                    ) { fileContents, filename ->
                        if (fileContents.isEmpty()) {
                            message.invoke("Empty file")
                        }
                        if (importMapping) {
                            mappingFile = fileContents
                            mappingText.value = filename
                            message.invoke("Mapping : $filename imported")
                        } else {
                            passFile = fileContents
                            passText.value = filename
                            message.invoke("Pass : $filename imported")
                        }
                        working.invoke(false)
                        viewModelScope.launch(Dispatchers.Main) {
                            noPassImport.value = true
                        }

                    }
                }
            } else {
                message.invoke("File read failed")
                working.invoke(false)
            }
        }
    }

    fun startImport(
        success: () -> Unit,
        putPasses: (List<PassesEntity>, List<PassListEntity>) -> Unit,
        error: (error: String) -> Unit
    ) {

        working.value = true
        viewModelScope.launch(Dispatchers.Default) {
            val mappingListType = object : TypeToken<List<PassListEntity>>() {}.type
            val passListType = object : TypeToken<List<PassesEntity>>() {}.type
            try {
                val mapping: List<PassListEntity> = Gson().fromJson(mappingFile, mappingListType)
                val passes: List<PassesEntity> = Gson().fromJson(passFile, passListType)

                //  checkMappingAndPasses(mapping ,passes)
                var checked = true
                viewModelScope.launch(Dispatchers.IO) {
                    for (i in passes.indices) {
                        if (mapping.size != passes.size || mapping[i].passId != passes[i].passId) {
                            viewModelScope.launch(Dispatchers.Main) {
                                inconsistentError.value = true

                            }

                            val toContinue = userInputChannel.receive()
                            if (toContinue) {
                                checked = true
                                break
                            } else {
                                checked = false
                                break
                            }
                        }
                    }


                    if (checked) {
                        putPasses.invoke(passes, mapping)
                        success.invoke()
                        mappingFile = ""
                        passFile = ""

                    } else {
                        error.invoke("import abort")
                        working.value = false
                    }
                }
            } catch (e: Exception) {
                error.invoke(e.message ?: "Error processing Files")
                working.value = false
            }


        }
    }


    /*    fun checkMappingAndPasses(mapping: List<PassListEntity>, passes: List<PassesEntity>) {
            viewModelScope.launch(Dispatchers.IO) {


        }*/
    /* fun putPasses(passes: List<PassesEntity>) {
         viewModelScope.launch {
             for (i in passes.indices) {
                 if (passesDao.getPass(passes[i].passId)?.passId != passes[i].passId) {
                     passesDao.insertNewPass(passes[i])
                 }
             }
             //passesDao.insertAllPass(passes)

         }
     }

     fun putPassList(passList: List<PassListEntity>) {
         viewModelScope.launch {
             //     passListDao.insertAllNewPass(passList)
             for (i in passList.indices) {
                 if (passListDao.getPassList(passList[i].passId) == null) {
                     passListDao.insertNewPass(passList[i])
                 }
             }
         }
     }*/


    fun importCloud(
        message: (String) -> Unit,
        error: () -> Unit,
        putPasses: (List<PassesEntity>, List<PassListEntity>) -> Unit,
        success: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                if (auth.currentUser!!.isEmailVerified) {
                    val db = FirebaseFirestore.getInstance()
                    var passList: List<PassListEntity>
                    val dbPassList: CollectionReference =
                        db.collection("PasswordManager").document(auth.currentUser!!.uid)
                            .collection("YourPass")

                    dbPassList.get().addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            passList = emptyList<PassListEntity>().toMutableList().apply {
                                task.result.forEach { query ->
                                    this.add(query.toObject())
                                }
                            }

                            if (passList.isNotEmpty()) {
                                val dbPass =
                                    db.collection("Passwords")
                                        .document(auth.currentUser!!.uid)
                                        .collection("YourPass")
                                var passes: List<PassesEntity>

                                dbPass.get().addOnCompleteListener { dbPassResult ->
                                    if (dbPassResult.isSuccessful && dbPassResult.result != null) {
                                        passes = emptyList<PassesEntity>().toMutableList().apply {
                                            dbPassResult.result.forEach { query ->
                                                this.add(query.toObject())
                                            }
                                        }
                                        if (passes.isNotEmpty()) {
                                            putPasses(passes, passList)
                                            success.invoke()
                                            if (passes.size != passList.size) {
                                                message.invoke("Some of the passwords were not fully available, ${passList.size - passes.size}")
                                            }
                                        } else {
                                            message.invoke("No passwords fetched , list size ${passList.size}")
                                            error.invoke()
                                        }
                                    } else {
                                        message.invoke("Error getting passwords")
                                        error.invoke()
                                    }
                                }
                            } else {
                                message.invoke("No passwords on cloud")
                                error.invoke()
                            }

                        } else {
                            message.invoke("Error getting password list")
                            error.invoke()
                        }
                    }

                } else {
                    message.invoke("Login first")
                    error.invoke()
                }
            } else {
                message.invoke("Login first")
                error.invoke()
            }


        }
    }

}