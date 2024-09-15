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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leo.nopasswordforyou.screen.keyManageScreen.HeadInfoText
import com.leo.nopasswordforyou.util.sorting.SortOrder
import com.leo.nopasswordforyou.util.sorting.Sorting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptions(
    selected: Sorting,
    sortOrder: SortOrder,
    onDialogueDismiss: () -> Unit,
    sort: (sort: Sorting, sortOrder: SortOrder) -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDialogueDismiss) {
        Box(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                var sorting by rememberSaveable {
                    mutableStateOf(selected)
                }
                var sortedBy by rememberSaveable {
                    mutableStateOf(sortOrder)
                }
                Text(
                    text = "Sort by",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(height = 10.dp)
                LazyColumn {
                    items(Sorting.entries.size) {
                        SelectRadio(
                            selected = sorting.ordinal == it, text = Sorting.entries[it].name
                        ) {
                            sorting = Sorting.entries[it]
                        }
                        Spacer(height = 10.dp)

                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HeadInfoText(
                                title = "Order by",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(width = 10.dp)
                            HorizontalDivider()
                        }
                        Spacer(height = 10.dp)
                    }
                    items(SortOrder.entries.size) {
                        SelectRadio(
                            selected = sortedBy.ordinal == it, text = SortOrder.entries[it].name
                        ) {
                            sortedBy = SortOrder.entries[it]
                        }
                        Spacer(height = 10.dp)
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onDialogueDismiss,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(text = "Cancel")
                            }
                            Spacer(width = 10.dp)
                            Button(
                                onClick = { sort(sorting, sortedBy) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(text = "Done")
                            }
                        }
                    }

                }

                Spacer(height = 20.dp)

            }
        }
    }
}