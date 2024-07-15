/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 02/06/24, 10:41 am
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

package com.leo.nopasswordforyou.components

import android.graphics.Bitmap
import android.os.Handler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.idapgroup.snowfall.snowmelt
import com.idapgroup.snowfall.types.FlakeType
import com.leo.nopasswordforyou.R


@Composable
fun KeyManageDialogue(
    onDialogueDismiss: () -> Unit, onNewKey: () -> Unit, onImportKey: () -> Unit
) {
    AlertDialog(onDismissRequest = onDialogueDismiss, confirmButton = {
        Row {
            FilledTonalButton(onClick = onNewKey) {
                Text(text = "New Key", fontFamily = FontFamily(Font(R.font.open_sans)))
            }
            Spacer(modifier = Modifier.padding(start = 20.dp))
            FilledTonalButton(onClick = onImportKey) {
                Text(text = "Import Key", fontFamily = FontFamily(Font(R.font.open_sans)))
            }
        }
    })
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateNewItem(
    title0: String,
    uid0: String,
    desc0: String,
    alias0: String,
    password0: String,
    arrayKeys: ArrayList<String>?,
    isUpdate: Boolean = false,
    onExit: () -> Unit,
    onDoneClick: (title: String, uid: String, desc: String, alias: String, keySelectedIndex: Int) -> Unit
) {
    AlertDialog(onDismissRequest = onExit, confirmButton = { /*TODO*/

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            val focusManager = LocalFocusManager.current
            val (a, b, c) = FocusRequester.createRefs()

            var title by rememberSaveable { mutableStateOf(title0) }
            var uid by rememberSaveable { mutableStateOf(uid0) }
            var desc by rememberSaveable { mutableStateOf(desc0) }


            var keySelectedIndex by remember { mutableIntStateOf(0) }


            OutlinedTextField(
                value = title,
                label = { Text(text = "Title") },
                onValueChange = { title = it },
                singleLine = true,
                suffix = { Text(text = "required") },
                modifier = Modifier
                    .focusRequester(a)
                    .focusProperties { next = b },

                shape = RoundedCornerShape(18.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                })

            )
            Spacer(modifier = Modifier.padding(vertical = 1.dp))
            OutlinedTextField(
                value = uid,
                label = { Text(text = "Uid") },
                onValueChange = { uid = it },
                singleLine = true,
                suffix = { Text(text = "optional") },
                shape = RoundedCornerShape(18.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(focusDirection = FocusDirection.Next)
                }),
                modifier = Modifier
                    .focusRequester(b)
                    .focusProperties {
                        next = c
                        previous = a
                    },
            )
            Spacer(modifier = Modifier.padding(vertical = 1.dp))
            OutlinedTextField(
                value = desc,
                label = { Text(text = "Details") },
                onValueChange = { desc = it },
                shape = RoundedCornerShape(18.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Previous
                ),
                keyboardActions = KeyboardActions(onPrevious = {
                    focusManager.moveFocus(focusDirection = FocusDirection.Previous)
                }),
                suffix = { Text(text = "optional") },
                modifier = Modifier
                    .focusRequester(c)
                    .focusProperties {
                        previous = b
                    },
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))



            if (isUpdate) {
                Row {
                    TextField(
                        value = "dlkfldflkdjlfdjfdfsdfg",
                        singleLine = true,
                        readOnly = true,
                        onValueChange = {},
                        shape = RoundedCornerShape(18.dp),
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.padding(start = 7.dp))
                    FilledTonalButton(
                        onClick = onExit,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),

                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_new_24),
                            contentDescription = "New password"
                        )
                    }

                }


            } else {
                LargeDropdownMenu(
                    modifier = Modifier,
                    label = arrayKeys!![keySelectedIndex],
                    items = arrayKeys,
                    onItemSelected = { index, _ ->
                        keySelectedIndex = index
                    })
            }


            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Row {


                FilledTonalButton(
                    onClick = onExit,
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),

                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_exit_to_app_24),
                        contentDescription = "Exit"
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 20.dp))
                FilledTonalButton(
                    onClick = {
                        onDoneClick.invoke(
                            title,
                            uid,
                            desc,
                            if (isUpdate) alias0 else arrayKeys!![keySelectedIndex],
                            keySelectedIndex
                        )

                    },
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),

                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_done_24),
                        contentDescription = "Done"
                    )

                }
            }
        }
    })
}


/*@Preview(
    device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420",
    showSystemUi = true, showBackground = true
)*/
@Composable
fun ShowPass(
    id: String,
    password: String,
    description: String,
    dismissDialog: () -> Unit,
    copy: (String) -> Unit,
    edit: () -> Unit
) {
    Dialog(onDismissRequest = { dismissDialog.invoke() }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                /*
                 Row(
                     horizontalArrangement = Arrangement.End,
                     modifier = Modifier.fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     FilledTonalButton(onClick = { }, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues()) {
                         Icon(
                             painter = painterResource(id = R.drawable.icon_build_24),
                             contentDescription = ""
                         )
                     }


                 }
                 */
                if (id.isNotEmpty()) {


                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = id, fontFamily = FontFamily(Font(R.font.open_sans)))
                        var copyPassIcon by rememberSaveable {
                            mutableIntStateOf(R.drawable.icon_copy_24)
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    copy.invoke(id)
                                    val handler = Handler()
                                    copyPassIcon = R.drawable.icon_done_24
                                    handler.postDelayed(
                                        { copyPassIcon = R.drawable.icon_copy_24 },
                                        1500
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues()
                            ) {
                                Icon(
                                    painter = painterResource(id = copyPassIcon),
                                    contentDescription = "",
                                    modifier = Modifier.sizeIn(maxWidth = 18.dp, maxHeight = 18.dp)
                                )
                            }

                        }


                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    var showPass by rememberSaveable {
                        mutableStateOf(false)
                    }

                    val data = listOf(
                        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.icon_spoiler1)),
                        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.icon_spoiler_2)),
                    )
                    Box(modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .clickable(interactionSource = remember {
                            MutableInteractionSource()
                        }, indication = null, enabled = true) { showPass = !showPass }) {
                        Column {
                            Text(
                                text = password,
                                modifier = Modifier,
                                fontFamily = FontFamily(Font(R.font.open_sans))
                            )
                            Spacer(modifier = Modifier.padding(top = 2.dp))
                        }

                        if (!showPass) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()

                                    .snowmelt(FlakeType.Custom(data), density = 1.0)
                                    .snowmelt(FlakeType.Custom(data), density = 1.0)
                                    .snowmelt(FlakeType.Custom(data), density = 1.0)
                                    .snowmelt(FlakeType.Custom(data), density = 1.0)
                                    .snowmelt(FlakeType.Custom(data), density = 1.0)
                                    .snowmelt(FlakeType.Custom(data), density = 1.0),
                                color = MaterialTheme.colorScheme.surface,
                            ) {}
                            //  Spacer( modifier = Modifier.fillMaxSize()))
                        }

                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var copyPassIcon by rememberSaveable {
                            mutableIntStateOf(R.drawable.icon_copy_24)
                        }
                        FilledTonalButton(
                            onClick = {
                                copy.invoke(password)
                                copy.invoke(id)
                                val handler = Handler()
                                copyPassIcon = R.drawable.icon_done_24
                                handler.postDelayed(
                                    { copyPassIcon = R.drawable.icon_copy_24 },
                                    1500
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues()
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = copyPassIcon
                                ),
                                contentDescription = "",
                                modifier = Modifier.sizeIn(maxWidth = 18.dp, maxHeight = 18.dp)
                            )
                        }
                    }


                }

                if (description.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = description, fontFamily = FontFamily(Font(R.font.open_sans)))
                    }
                }




                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {

                    FilledTonalButton(
                        onClick = { dismissDialog.invoke() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues()
                    ) {
                        Text(text = "Close", fontFamily = FontFamily(Font(R.font.open_sans)))
                    }
                    Spacer(modifier = Modifier.padding(start = 20.dp))
                    FilledTonalButton(
                        onClick = { edit.invoke() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues()
                    ) {
                        Text(
                            text = "Edit ", fontFamily = FontFamily(Font(R.font.open_sans))
                        )
                    }


                }
            }
        }
    }

}


@Composable
fun NewKeyDialogue(onCreateKey: (keyName: String) -> Unit, onDialogueDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDialogueDismiss, confirmButton = {
        Column {
            var alias by rememberSaveable {
                mutableStateOf("")
            }
            TextField(value = alias,
                onValueChange = { alias = it },
                label = { Text(text = "Key name") },
                singleLine = true,
                supportingText = { Text(text = "must be unique") })
            Spacer(modifier = Modifier.padding(top = 20.dp))



            FilledTonalButton(onClick = {
                onCreateKey(alias)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Create", fontFamily = FontFamily(Font(R.font.open_sans)))
            }

        }
    })


}


@Composable
fun Loader(message: String) {

    Column(modifier = Modifier.fillMaxSize()) {
        AlertDialog(onDismissRequest = {}, text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(60.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(text = message, fontFamily = FontFamily(Font(R.font.open_sans)))


            }
        }, confirmButton = {})
    }


}

data class ImageState(val image: Bitmap, var rotationAngle: Float = 0f)






