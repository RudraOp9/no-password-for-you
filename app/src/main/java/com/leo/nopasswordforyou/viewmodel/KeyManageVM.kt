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
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import com.leo.nopasswordforyou.secuirity.Security
import com.leo.nopasswordforyou.util.FilesIO.importFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyManageVM @Inject constructor(
) : ViewModel() {
    val working: MutableState<Boolean> = mutableStateOf(false)
    var createNewKey: MutableState<Boolean> = mutableStateOf(false)
    var createKeyAlias: MutableState<String> = mutableStateOf("")

    private lateinit var launcher: ManagedActivityResultLauncher<Intent, ActivityResult>


    //todo remove alias from keystore too


    fun createKey(
        key: String,
        context: Context,
        checkAlias: (String) -> Boolean,
        setAlias: (String) -> Unit,
        snackBar: (String) -> Unit
    ) {
        if (key.trim().isEmpty()) {
            snackBar.invoke("Empty key name")
            createNewKey.value = true
            stopWorking()
            return
        }
        if (checkAlias(key)) {
            snackBar.invoke("Key should be unique")
            createNewKey.value = true
            stopWorking()
            return
        }
        if (Build.VERSION.SDK_INT <= 28) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                (context as Activity).requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
                snackBar.invoke("Grant write permission and try again")
                stopWorking()
                return
            }
        }
        viewModelScope.launch(context = Dispatchers.Default) {
            val security = Security(context)
            val newKey = security.newKey(key)
            if (newKey == "done") {
                setAlias.invoke(key)

                snackBar.invoke("Key saved to Downloads. Keep it secure! See FAQ for more.")
                stopWorking()
            } else {
                security.removeKey(key, {}, {})
                stopWorking()
                //createNewKey.value = true
                snackBar.invoke(newKey)

            }
        }
    }

    fun deleteKey(context: Context, alias: String, error: (String) -> Unit, success: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            Security(context).removeKey(alias, error = error, success = success)


        }
    }

    @Composable
    fun SelectAndReadFile(
        message: (String) -> Unit,
        uploadAlias: (String) -> Unit,
        work: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            work.invoke(true)
            if (result.resultCode == Activity.RESULT_OK) {
                viewModelScope.launch(Dispatchers.IO) {
                    importFile(
                        result = result,
                        message = {
                            message.invoke(it)
                            work.invoke(false)
                        },
                        context = context,
                    ) { fileContents, _ ->
                        Security(context).importKey(fileContents) { msg, exitCode ->
                            if (exitCode == 0) {
                                uploadAlias.invoke(msg)
                                message.invoke("key: $msg successfully imported")
                            } else {
                                message.invoke(msg)
                                work.invoke(false)
                            }
                        }
                    }
                }
            } else {
                message.invoke("File read failed")
                work.invoke(false)
            }
        }

        // Trigger the file selection (e.g., from a button click)
    }

    fun launchSelectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*" // Allow all file types (you can be more specific if needed)
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("application/octet-stream")

            ) // Add .ppk MIME type
        }
        launcher.launch(intent)
    }


    fun startWorking() {
        working.value = true
    }

    fun stopWorking() {
        working.value = false
    }
}
