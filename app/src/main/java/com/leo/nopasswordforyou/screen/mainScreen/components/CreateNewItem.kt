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

import android.content.ClipboardManager
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.DropDownSelector
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.database.alias.AliasEntity
import com.leo.nopasswordforyou.viewmodel.MainActivityVm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun CreateNewItem(
    coroutine: CoroutineScope = rememberCoroutineScope(),
    title0: String,
    uid0: String,
    desc0: String,
    alias0: String,
    password0: String,
    arrayKeys: List<AliasEntity>?,
    isUpdate: Boolean = false,
    vm: MainActivityVm,
    onExit: () -> Unit,
    onDoneClick: (title: String, uid: String, desc: String, alias: String, keySelectedIndex: Int, pass: String, uploadToCloud: Boolean) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var uploadToCloud by rememberSaveable {
        mutableStateOf(false)
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        sheetState = sheetState,
        shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0),
        onDismissRequest = {
            onExit.invoke()
        },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
            securePolicy = SecureFlagPolicy.Inherit
        )
    ) {
        val keyboardVisibility = WindowInsets.isImeVisible
        BackHandler {
            if (!keyboardVisibility) {
                onExit.invoke()

            }
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp, horizontal = 20.dp)

        ) {

            val focusManager = LocalFocusManager.current

            val (a, b, c) = FocusRequester.createRefs()

            var title by rememberSaveable { mutableStateOf(title0) }
            var uid by rememberSaveable { mutableStateOf(uid0) }
            var desc by rememberSaveable { mutableStateOf(desc0) }
            val pass = remember { TextFieldState(initialText = password0) }


            var keySelectedIndex by remember { mutableIntStateOf(0) }


            OutlinedTextField(
                value = title,
                label = { Text(text = "Title") },
                onValueChange = { title = it },
                singleLine = true,
                suffix = {
                    Text(
                        text = "required",
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                },
                modifier = Modifier
                    .focusRequester(a)
                    .focusProperties { next = b },

                shape = RoundedCornerShape(12.dp),
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
                label = {
                    Text(
                        text = "Uid",
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                },
                onValueChange = { uid = it },
                singleLine = true,
                suffix = { Text(text = "optional") },
                shape = RoundedCornerShape(12.dp),
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
                label = {
                    Text(
                        text = "Details",
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                },
                onValueChange = { desc = it },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    // imeAction = ImeAction.Previous
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
                Row(verticalAlignment = Alignment.CenterVertically) {

                    BasicSecureTextField(
                        state = pass,
                        modifier = Modifier
                            .background(color = TextFieldDefaults.colors().focusedContainerColor)
                            .border(
                                1.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(12)
                            )
                            .padding(vertical = 20.dp, horizontal = 30.dp),

                        )
                    Spacer(width = 10.dp)
                    Text(
                        text = "Paste",
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        modifier = Modifier
                            .background(color = TextFieldDefaults.colors().focusedContainerColor)
                            .border(
                                1.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(12)
                            )
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            ) {
                                val clipboard = context
                                    .getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                                if (clipboard.hasPrimaryClip() &&
                                    clipboard.primaryClip?.getItemAt(0)?.text != null
                                ) {
                                    pass.edit {
                                        this.delete(0, this.length)
                                        this.insert(
                                            0,
                                            clipboard.primaryClip?.getItemAt(0)?.text.toString()
                                        )
                                    }
                                    // Proceed with filtering
                                }
                            }
                    )
                }

            } else {


                DropDownSelector(
                    list = arrayKeys!!,
                    selectedIndex = keySelectedIndex
                ) { index, _ ->
                    keySelectedIndex = index
                }

            }


            Spacer(height = 20.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text(
                    text = if (isUpdate) "Update on cloud" else "Save on cloud",
                    modifier = Modifier.padding(start = 10.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Checkbox(
                        checked = uploadToCloud,
                        onCheckedChange = { uploadToCloud = it },
                        enabled = vm.isLoggedIn() && vm.isMailVerified()
                    )
                }
            }
            if (!vm.isLoggedIn() || !vm.isMailVerified()) {
                Column(
                    horizontalAlignment = Alignment.Start, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        text = "Login to save on cloud",
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        color = MaterialTheme.typography.labelMedium.color
                    )
                }
            }


            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Row {
                Button(
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = {
                        coroutine.launch(Dispatchers.Main) {
                            sheetState.hide()
                            onExit()

                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),

                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_exit_to_app_24),
                        contentDescription = "Exit"
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 20.dp))
                Button(
                    onClick = {
                        onDoneClick.invoke(
                            title,
                            uid,
                            desc,
                            if (isUpdate) alias0 else arrayKeys!![keySelectedIndex].alias,
                            keySelectedIndex,
                            pass.text.toString(),
                            uploadToCloud
                        )

                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),

                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_done_24),
                        contentDescription = "Done"
                    )

                }
            }
        }
    }

}