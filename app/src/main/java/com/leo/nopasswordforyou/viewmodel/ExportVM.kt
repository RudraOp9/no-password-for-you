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
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.util.FilesIO.saveFileToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExportVM @Inject constructor(

) : ViewModel() {
    val working: MutableStateFlow<Boolean> = MutableStateFlow(false)

    var startSelection: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var selectAll: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var selected: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())

    /*    fun getPass(keyId: String): Deferred<PassesEntity> {
            return viewModelScope.async {
                return@async passesDao.getPass(keyId) ?: PassesEntity(
                    passId = "-1",
                    userId = "",
                    password = "",
                    alias = "null"
                )
            }
        }*/


    fun export(
        context: Context,
        vault: List<PassListEntity>,
        getPass: CoroutineScope.(String) -> Deferred<PassesEntity>
    ) {
        working.value = true
        if (Build.VERSION.SDK_INT <= 28) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Grant write permission and try again", Toast.LENGTH_SHORT)
                    .show()
                working.value = false
                (context as Activity).requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )

                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val passMap: MutableList<PassListEntity> = mutableListOf()
            val passes: MutableList<PassesEntity> = mutableListOf()

            for (index in selected.value) {
                val vmPass = vault[index]
                passMap.add(vmPass)
                val pass = getPass(vmPass.passId).await()
                passes.add(pass)
            }

            val passMapStr = Gson().toJson(passMap)
            val passStr = Gson().toJson(passes)
            viewModelScope.launch {
                saveFileToDownloads(
                    context, "PassMap-export${
                        SimpleDateFormat(
                            "HH-mm-ss-dd-MM-yy",
                            Locale.getDefault()
                        ).format(Date())
                    }.mapping", passMapStr, "application/octet-stream",
                    success = {
                        viewModelScope.launch(Dispatchers.IO) {
                            saveFileToDownloads(
                                context, "Passes-export${
                                    SimpleDateFormat(
                                        "HH-mm-ss-dd-MM-yy",
                                        Locale.getDefault()
                                    ).format(Date())
                                }.pass", passStr, "application/octet-stream",
                                success = {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        working.value = false
                                        Toast.makeText(
                                            context,
                                            "File Saved in Downloads",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                            ) {
                                working.value = false
                            }
                        }

                    }
                ) {
                    working.value = false
                }

            }
        }
    }
}