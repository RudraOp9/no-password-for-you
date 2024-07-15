/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 03/06/24, 11:20 am
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

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.material.color.MaterialColors.ALPHA_DISABLED
import com.google.android.material.color.MaterialColors.ALPHA_FULL

@Composable
fun <T> LargeDropdownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    textAlign: TextAlign = TextAlign.Start,
    simple: Boolean = true,
    selectedItemToString: (T) -> String = { it.toString() },

    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        if (simple) {
            OutlinedTextField(
                label = { Text(label) },
                value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
                enabled = enabled,
                modifier = modifier.align(Alignment.Center),
                textStyle = TextStyle(textAlign = textAlign),
                trailingIcon = {
                    val icon =
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                    Icon(icon, "")
                },
                onValueChange = { },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

        } else {

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {

                BasicTextField(
                    value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) }
                        ?: "Select",
                    enabled = enabled,
                    modifier = modifier.align(Alignment.CenterVertically),
                    textStyle = TextStyle(textAlign = textAlign),
                    onValueChange = { },
                    readOnly = true,
                )
                val icon =
                    if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                Icon(icon, "", modifier = Modifier.padding(start = 10.dp))
            }

        }
        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null, enabled = enabled) { expanded = !expanded },
            color = Color.Transparent,
        ) {}


        if (expanded) {
            Dialog(
                onDismissRequest = { expanded = false },
                properties = DialogProperties(dismissOnClickOutside = true)
            ) {

                Column(

                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable(interactionSource = remember {
                            MutableInteractionSource()
                        }, indication = null, enabled = enabled) { expanded = !expanded }
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                    ) {
                        val listState = rememberLazyListState()
                        if (selectedIndex > -1) {
                            LaunchedEffect("ScrollToSelected") {
                                listState.scrollToItem(index = selectedIndex)
                            }
                        }

                        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                            if (notSetLabel != null) {
                                item {
                                    LargeDropdownMenuItem(
                                        text = notSetLabel,
                                        selected = false,
                                        enabled = false,
                                        onClick = { },
                                    )
                                }
                            }
                            itemsIndexed(items) { index, item ->
                                val selectedItem = index == selectedIndex
                                drawItem(
                                    item,
                                    selectedItem,
                                    true
                                ) {
                                    onItemSelected(index, item)
                                    expanded = false
                                }

                                if (index < items.lastIndex) {
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = ALPHA_FULL)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_FULL)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}