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

package com.leo.nopasswordforyou.screen.mainScreen.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.screen.mainScreen.CustomSet
import com.leo.nopasswordforyou.viewmodel.MainActivityVm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GenPass(
    vm: MainActivityVm,
    coroutine: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    generatePass: (capAlpha: Int, smallAlpha: Int, numbers: Int, symbols: Int) -> String,
    setPass: (pass: String) -> Unit,
    onCreatePass: () -> Unit
) {

    var passWord by rememberSaveable {
        mutableStateOf(vm.passWord)
    }
    var customSettings by rememberSaveable {
        mutableStateOf(false)
    }
    var alphaCap by rememberSaveable {
        mutableIntStateOf(4)
    }
    var alphaCapVisi by rememberSaveable {
        mutableStateOf(true)
    }
    var smallCap by rememberSaveable {
        mutableIntStateOf(5)
    }
    var smallCapVisi by rememberSaveable {
        mutableStateOf(true)
    }
    var symbol by rememberSaveable {
        mutableIntStateOf(2)
    }
    var symbolVisi by rememberSaveable {
        mutableStateOf(true)
    }
    var numbers by rememberSaveable {
        mutableIntStateOf(5)
    }
    var numbersVisi by rememberSaveable {
        mutableStateOf(true)
    }
    //generate
    Column(
        Modifier
            .padding(horizontal = 26.dp)
            .verticalScroll(
                rememberScrollState()
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(height = 10.dp)
        Box(modifier = Modifier.width(IntrinsicSize.Min)) {
            //   animateIntAsState(targetValue = 5)


            TextField(
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = typography.titleMedium.fontSize,
                    fontWeight = typography.titleMedium.fontWeight,
                ),
                modifier = Modifier
                    .width(360.dp)
                    .height(100.dp),
                /* .border(2.dp, TextFieldDefaults.colors().focusedIndicatorColor, shape = RoundedCornerShape(12)*/
                shape = RoundedCornerShape(
                    topEndPercent = 12,
                    topStartPercent = 12
                ),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = colorScheme.surfaceContainerHighest,
                ),
                value = passWord,
                onValueChange = {
                    passWord = it
                    setPass(it)
                },
                supportingText = {
                    Text(
                        text = "Length : ${
                            if (customSettings) {
                                (if (alphaCapVisi) alphaCap else 0) +
                                        (if (symbolVisi) symbol else 0) +
                                        (if (numbersVisi) numbers else 0) +
                                        (if (smallCapVisi) smallCap else 0)
                            } else 16
                        }",
                        modifier = Modifier.height(20.dp)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "",
                        Modifier.clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {

                            coroutine.launch {
                                animate(
                                    Int.VectorConverter,
                                    passWord.length,
                                    0,
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        easing = LinearEasing
                                    )
                                ) { _, _ ->
                                    passWord = passWord.dropLast(1)
                                }
                                passWord = if (customSettings) {
                                    generatePass.invoke(
                                        (if (alphaCapVisi) alphaCap else 0),
                                        (if (smallCapVisi) smallCap else 0),
                                        (if (numbersVisi) numbers else 0),
                                        (if (symbolVisi) symbol else 0)
                                    )

                                } else {
                                    generatePass(4, 5, 5, 2)
                                }
                            }

                        }
                    )
                },
            )


        }
        Spacer(height = (10.dp))
        Surface(
            modifier = Modifier
                .width(360.dp)
                .height(80.dp),
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (customSettings) 0.dp else
                    12.dp,
                bottomEnd = if (customSettings) 0.dp else 12.dp
            ),
            color = colorScheme.surfaceContainerHighest
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Custom settings",
                    fontWeight = typography.titleMedium.fontWeight,
                    fontSize = typography.titleMedium.fontSize,
                    color = colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Switch(
                        checked = customSettings,
                        onCheckedChange = {
                            customSettings = it

                        })
                }


            }
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(1.dp))

        AnimatedVisibility(visible = customSettings) {


            Surface(
                modifier = Modifier
                    .width(360.dp)
                    .heightIn(min = 80.dp, max = 600.dp),
                shape = RoundedCornerShape(
                    bottomEnd = 12.dp,
                    bottomStart = 12.dp
                ),
                color = colorScheme.surfaceContainerHighest
            ) {

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    /*  CustomPass(vm.genPassVars.collectAsState(),vm.genPassVisibility.collectAsState()){
                          vm.updateTotalText()
                      }*/

                    LazyColumn {
                        items(4) { index ->
                            CustomSet(
                                when (index) {
                                    0 -> "Capital letters"
                                    1 -> "Small letters"
                                    2 -> "Numbers"
                                    else -> "Symbols"
                                },
                                when (index) {
                                    0 -> if (alphaCapVisi) alphaCap else 0
                                    1 -> if (smallCapVisi) smallCap else 0
                                    2 -> if (numbersVisi) numbers else 0
                                    else -> if (symbolVisi) symbol else 0
                                },
                                when (index) {
                                    0 -> alphaCapVisi
                                    1 -> smallCapVisi
                                    2 -> numbersVisi
                                    else -> symbolVisi
                                }, {
                                    when (index) {
                                        0 -> alphaCapVisi = it
                                        1 -> smallCapVisi = it
                                        2 -> numbersVisi = it
                                        else -> symbolVisi = it
                                    }
                                }
                            ) { _, item ->
                                when (index) {
                                    0 -> alphaCap = item
                                    1 -> smallCap = item
                                    2 -> numbers = item
                                    else -> symbol = item
                                }
                            }
                        }
                    }
                    /*LazyColumn {
                        items(4) { index ->

                        }
                    }*/


                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {

                    onCreatePass()
                },
                shape = RoundedCornerShape(12),
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 8.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_cloud_save),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Save")
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = { vm.copyPassword(context = context) },
                shape = RoundedCornerShape(12),
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 8.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = vm.passwordCopyIcon.collectAsState().value),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Copy")
                }
            }
        }


    }
}