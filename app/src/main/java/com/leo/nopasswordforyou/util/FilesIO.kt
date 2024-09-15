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

package com.leo.nopasswordforyou.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object FilesIO {


    suspend fun importFile(
        result: ActivityResult,
        fileExtension: String = "",
        message: (String) -> Unit,
        context: Context,
        success: (result: String, fileName: String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            result.data?.data?.let { uri ->
                //   message.invoke("Working.... please wait")
                context.contentResolver.query(uri, null, null, null, null)?.use { file ->
                    if (file.moveToFirst()) {
                        val nameIndex = file.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            if (file.getString(nameIndex)
                                    .substringAfterLast('.', "") != fileExtension
                                && fileExtension != ""
                            ) {
                                message.invoke("Invalid file")
                                //     work.invoke(false)

                            } else {
                                context.contentResolver.openInputStream(uri)?.use {
                                    val fileContents = it.bufferedReader()
                                        .use { it0 -> it0.readText() }

                                    success.invoke(fileContents, file.getString(nameIndex))
                                    this.launch(Dispatchers.Default) {
                                        /**/
                                    }
                                }

                            }
                        } else {
                            message.invoke("invalid file")
                            //work.invoke(false)

                        }
                    } else {
                        message.invoke("invalid file")
                        // work.invoke(false)
                    }

                }


            }
        }
    }

    suspend fun saveFileToDownloads(
        context: Context,
        fileName: String,
        fileContent: String,
        mimeType: String,
        success: () -> Unit,
        error: () -> Unit

    ) {
        try {
            Log.d("ImportTAG", "trying to save an file")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType) // Adjust MIME type as needed
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(fileContent.toByteArray())
                    }
                }
                success.invoke()
            } else {
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                file.writeText(fileContent)
                success.invoke()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "error saving file", Toast.LENGTH_SHORT).show()
                error.invoke()
            }


        }

    }
}