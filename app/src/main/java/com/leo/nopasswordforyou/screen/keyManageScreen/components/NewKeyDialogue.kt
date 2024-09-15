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

package com.leo.nopasswordforyou.screen.keyManageScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewKeyDialogue(
    visible: Boolean,
    onCreateKey: (keyName: String) -> Unit,
    key: MutableState<String>,
    onDialogueDismiss: () -> Unit
) {
    if (visible) {
        BasicAlertDialog(
            modifier = Modifier,
            onDismissRequest = onDialogueDismiss,
        ) {
            Box(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(12)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    OutlinedTextField(value = key.value,
                        onValueChange = { key.value = it },
                        label = { Text(text = "Key name") },
                        singleLine = true,
                        supportingText = { Text(text = "Required to distinguish keys") })
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                    Button(
                        shape = RoundedCornerShape(12), onClick = {
                            onCreateKey(key.value)

                        }, modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Create")
                    }
                }
            }
        }

    }
}