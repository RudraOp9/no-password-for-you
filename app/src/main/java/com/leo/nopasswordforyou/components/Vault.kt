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

package com.leo.nopasswordforyou.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Vault(
    modifier: Modifier = Modifier,
    selection: Boolean,
    selected: Set<Int>,
    onSelectionChange: ((Boolean, Int) -> Unit),
    onItemHold: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    items: State<List<PassListEntity>>
) {


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (items.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Vault is empty !!",
                    fontSize = typography.titleSmall.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
        ) {
            item {
                Spacer(height = 10.dp)
            }
            items(items.value.size,
                key = {
                    items.value[it].passId
                }
            ) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .width(360.dp)
                            .height(80.dp)
                            .background(colorScheme.surfaceContainerHighest, RoundedCornerShape(12))
                            .combinedClickable(remember {
                                MutableInteractionSource()
                            }, indication = null, onLongClick = {
                                onItemHold.invoke(item)
                            }) {
                                if (selection) {
                                    onSelectionChange.invoke(selected.contains(item), item)
                                    //  isSelected = !isSelected
                                } else {
                                    onItemClick.invoke(item)
                                }
                            },
                    ) {


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()

                                .padding(start = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(0.7f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${items.value[item].title[0]}",
                                    fontSize = typography.titleLarge.fontSize,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            colorScheme.primary,
                                            CircleShape
                                        )
                                        .wrapContentHeight(Alignment.CenterVertically),
                                    color = colorScheme.onPrimary
                                )
                                androidx.compose.foundation.layout.Spacer(
                                    modifier = Modifier.width(
                                        16.dp
                                    )
                                )


                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = items.value[item].title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = typography.titleMedium.fontSize,
                                        color = colorScheme.onSurface
                                    )
                                    if (items.value[item].desc.isNotEmpty()) {
                                        androidx.compose.foundation.layout.Spacer(
                                            modifier = Modifier.height(
                                                4.dp
                                            )
                                        )
                                        Text(
                                            text = items.value[item].desc,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = typography.labelMedium.fontSize,
                                            color = colorScheme.onSurface
                                        )
                                    }

                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.3f)
                                    .padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedVisibility(visible = !selection) {
                                    Column(modifier = Modifier.padding(end = 10.dp)) {
                                        Text(
                                            text = items.value[item].alias,
                                            fontSize = typography.labelSmall.fontSize,
                                            color = colorScheme.outline
                                        )
                                        Spacer(height = 5.dp)
                                        Text(
                                            text = SimpleDateFormat(
                                                "dd/MM/yy",
                                                Locale.getDefault()
                                            ).format(items.value[item].lastModify),
                                            fontSize = typography.labelSmall.fontSize,
                                            color = colorScheme.outline
                                        )
                                    }
                                }
                                AnimatedVisibility(visible = selection) {
                                    Checkbox(
                                        checked = selected.contains(item) || selected.contains(-1),
                                        onCheckedChange = {
                                            onSelectionChange.invoke(
                                                selected.contains(item),
                                                item
                                            )
                                            // isSelected = it
                                        },
                                        modifier = Modifier
                                    )
                                }

                            }
                        }
                    }
                    Spacer(height = 10.dp)

                }
            }
        }
    }
}