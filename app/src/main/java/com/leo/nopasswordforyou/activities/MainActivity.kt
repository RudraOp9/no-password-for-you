/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 21/02/24, 6:44 pm
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.databinding.ActivityMainBinding
import com.leo.nopasswordforyou.databinding.AddAliasBinding
import com.leo.nopasswordforyou.databinding.CustomKeysetBinding
import com.leo.nopasswordforyou.helper.checkExternalWritePer
import com.leo.nopasswordforyou.secuirity.Security
import com.leo.nopasswordforyou.viewmodel.MainActivityVm
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vm: MainActivityVm
    private val requestPermissionLauncher =
        this.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { _: Boolean ->

        }
    private val filePickerLauncher =
        this.registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                contentResolver.openInputStream(it)?.use { inputStream ->

                    val fileContents = inputStream.bufferedReader()
                        .use { it0 -> it0.readText() }
                    Security(this).importKey(fileContents) { result ->
                        if (result != "error") {
                            Snackbar.make(binding.root, "Key successfully imported", 3500).show()
                            vm.setAlias(result)
                        } else {
                            Snackbar.make(binding.root, "Error , contact support", 3500).show()
                        }
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        vm = ViewModelProvider(this)[MainActivityVm::class.java]

        binding.compose.setContent {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(

                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Image(
                        painterResource(R.drawable.icon_key_24),
                        null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable {
                                val keyBind = CustomKeysetBinding.inflate(layoutInflater)


                                val keySetting: AlertDialog = alertDialogueBuilder(keyBind.root)
                                keyBind.addKey.setOnClickListener {
                                    filePickerLauncher.launch(
                                        arrayOf("*/*")
                                    )
                                }
                                keyBind.newKey.setOnClickListener {
                                    val ksnBind = AddAliasBinding.inflate(layoutInflater)
                                    val keySettingNewKey = alertDialogueBuilder(ksnBind.root)


                                    var exit = false
                                    ksnBind.addKey.setOnClickListener {
                                        if (exit) {
                                            keySettingNewKey.dismiss()
                                            keySetting.dismiss()
                                        } else if (checkExternalWritePer(
                                                this@MainActivity,
                                                requestPermissionLauncher
                                            )
                                        ) {
                                            if (ksnBind.alias.text
                                                    .toString()
                                                    .trim()
                                                    .isNotEmpty()
                                            ) {
                                                val security = Security(
                                                    this@MainActivity
                                                )
                                                val result =
                                                    security.newKey(ksnBind.alias.text.toString())
                                                if (result == "done") {
                                                    ksnBind.addKey.text = "Exit"
                                                    exit = true
                                                    vm.setAlias(ksnBind.alias.text.toString())
                                                    ksnBind.infoText.text =
                                                        "An new Key with the alias : ${ksnBind.alias.text.toString()} has been created and has been saved to \n'Download/noPassWordForYou/${ksnBind.alias.text.toString()}.ppk' \nNever Share the file to someone else and keep it private.\nSee Help section for More Info !"
                                                }
                                                Toast
                                                    .makeText(
                                                        this@MainActivity,
                                                        result,
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else Toast
                                                .makeText(
                                                    this@MainActivity,
                                                    "empty alias",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                }
                            },
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }




                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                    ExtendedFloatingActionButton(onClick = {
                        startActivity(
                            Intent(
                                this@MainActivity, GeneratePass::class.java
                            )
                        )
                    }) {
                        Text("Generate Password")
                    }
                    Spacer(Modifier.padding(top = 30.dp))
                    ExtendedFloatingActionButton({
                        startActivity(
                            Intent(
                                this@MainActivity, login_page::class.java
                            )
                        )
                    }) {
                        Text("Your Passwords")
                    }
                }
            }
        }


        /*  binding.generatePass.setOnClickListener {
              startActivity(
                  Intent(
                      this@MainActivity, GeneratePass::class.java
                  )
              )
          }
          binding!!.activityLogin.setOnClickListener {
              startActivity(
                  Intent(
                      this@MainActivity, login_page::class.java
                  )
              )
          }*/
    }

    private fun alertDialogueBuilder(view: View): AlertDialog {
        val a = MaterialAlertDialogBuilder(this).setView(view).create()
        a.setCancelable(true)
        a.setCanceledOnTouchOutside(true)
        a.show()
        return a
    }
}

