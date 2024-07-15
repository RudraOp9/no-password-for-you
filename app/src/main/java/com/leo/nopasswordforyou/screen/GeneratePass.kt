/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 13/07/24, 1:37 pm
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

package com.leo.nopasswordforyou.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.snackbar.Snackbar
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.CreateNewItem
import com.leo.nopasswordforyou.components.LargeDropdownMenu
import com.leo.nopasswordforyou.components.Loader
import com.leo.nopasswordforyou.viewmodel.GeneratePassVM


@Composable
fun GeneratePass(vm: GeneratePassVM) {

    var placing by rememberSaveable {
        mutableFloatStateOf(0F)
    }

    var isCustom by rememberSaveable {
        mutableStateOf(false)
    }
    var createNewItem by rememberSaveable {
        mutableStateOf(false)
    }
    var copyPassIcon by rememberSaveable {
        mutableStateOf(R.drawable.icon_copy_24)
    }
    var showLoader by rememberSaveable {
        mutableStateOf(false)
    }
    var message by rememberSaveable {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val view = LocalView.current

    Column(Modifier.fillMaxSize()) {

        TextField(
            value = vm.passWord.value,
            singleLine = true,
            onValueChange = { vm.passWord.value = it },
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    if (!vm.isLoggedIn()) {
                        Toast.makeText(context, "Login First !", Toast.LENGTH_SHORT).show()
                        return@FilledTonalButton
                    }

                    if (vm.aliases.isEmpty()) {
                        Toast.makeText(context, "Create an key first", Toast.LENGTH_SHORT).show()
                        return@FilledTonalButton
                    }

                    createNewItem = true


                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.onGloballyPositioned {
                    placing = it.localToWindow(Offset.Zero).x
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_save_24),
                    contentDescription = "save password",
                    modifier = Modifier
                        .padding(2.dp)
                )
            }

            FilledTonalButton(onClick = { vm.genNewPass() }, shape = RoundedCornerShape(10.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_regenerate_24),
                    contentDescription = "save password",
                    modifier = Modifier.padding(2.dp)
                )
            }
            FilledTonalButton(
                onClick = {
                    val clipboard =
                        context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copied Text", vm.passWord.value)
                    clipboard.setPrimaryClip(clip)
                    val handler = Handler()
                    copyPassIcon = R.drawable.icon_done_24
                    handler.postDelayed(
                        { copyPassIcon = R.drawable.icon_copy_24 },
                        1500
                    )
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(id = copyPassIcon),
                    contentDescription = "save password",
                    modifier = Modifier.padding(2.dp)
                )
            }
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = with(LocalDensity.current) { placing.toDp() },
                    top = 20.dp,
                    end = with(
                        LocalDensity.current
                    ) { placing.toDp() })
        ) {


            HorizontalDivider(Modifier.padding(top = 10.dp))



            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "Custom Settings",
                    fontSize = 19.sp,
                    color = if (isCustom) Color.Unspecified else Color.Unspecified.copy(0.5f)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Switch(checked = isCustom, onCheckedChange = { isCustom = it })
                }
            }
            if (isCustom) {
                vm.updateTotalText()
                Row(Modifier.padding(top = 15.dp)) {
                    Text(text = "Total Length")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(text = vm.total.value, modifier = Modifier.padding(end = 0.dp))
                        Icon(
                            Icons.Outlined.ArrowDropDown,
                            "",
                            modifier = Modifier.padding(start = 10.dp),
                            tint = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0f)
                        )
                    }
                }
                HorizontalDivider(
                    Modifier.padding(top = 10.dp),
                    thickness = 1.dp,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )

                PassValues("Capitals", vm.alphaCapLength.toString()) {
                    vm.alphaCapLength = it.toByte().coerceIn(-100, 100)
                    vm.updateTotalText()
                }
                HorizontalDivider(
                    Modifier.padding(top = 10.dp),
                    thickness = 1.dp,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )
                PassValues("Smalls", vm.alphaSmallLength.toString()) {
                    vm.alphaSmallLength = it.toByte().coerceIn(-100, 100)
                    vm.updateTotalText()
                }
                HorizontalDivider(
                    Modifier.padding(top = 10.dp),
                    thickness = 1.dp,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )

                PassValues("Numbers", vm.numberslen.toString()) {

                    vm.numberslen = it.toByte().coerceIn(-100, 100)
                    vm.updateTotalText()
                }
                HorizontalDivider(
                    Modifier.padding(top = 10.dp),
                    thickness = 1.dp,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )

                PassValues("Symbols", vm.specialSymbol.toString()) {
                    vm.specialSymbol = it.toByte().coerceIn(-100, 100)
                    vm.updateTotalText()
                }
            } else {
                vm.numberslen = 4.toByte()
                vm.alphaCapLength = 4.toByte()
                vm.alphaSmallLength = 6.toByte()
                vm.specialSymbol = 2.toByte()
                vm.passLength = 16.toByte()
            }


            if (createNewItem) {
                CreateNewItem(
                    title0 = "",
                    uid0 = "",
                    desc0 = "",
                    alias0 = "",
                    password0 = vm.passWord.value,
                    arrayKeys = vm.aliases,
                    onExit = {
                        createNewItem = false
                    }) { title, uid, desc, alias, keySelectedIndex ->

                    if (title.isNotEmpty()) {
                        showLoader = true
                        message = "Securing password"

                        vm.selectedAlias = keySelectedIndex
                        val encPass = vm.encryptPass(context, vm.passWord.value) {
                            Snackbar.make(view, it, 3000).show()
                            showLoader = false
                        }
                        if (encPass != null) {
                            vm.putPass(encPass, title, desc, uid, context) {
                                if (it.equals(0)) {
                                    showLoader = false
                                    createNewItem = false
                                }
                            }
                        }
                    } else {
                        Snackbar.make(view, "Empty title", 2000).show()
                        showLoader = false
                    }
                }
            }

            if (showLoader) {
                Loader(message = message)
            }


        }


    }
}


@Composable
fun PassValues(input: String, value: String, changeValue: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = input)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            LargeDropdownMenu(
                selectedIndex = value.toInt(),
                simple = false,
                textAlign = TextAlign.End,
                label = value,
                items = arrayOf(
                    "0",
                    "01",
                    "02",
                    "03",
                    "04",
                    "05",
                    "06",
                    "07",
                    "08",
                    "09",
                    "10",
                    "11",
                    "12",
                    "13",
                    "14",
                    "15"
                ).toList(),
                onItemSelected = { Int, item ->
                    changeValue.invoke(item)
                })

        }
    }
}