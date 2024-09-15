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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leo.nopasswordforyou.database.alias.AliasEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSelector(
    list: List<AliasEntity>,
    selectedIndex: Int,
    onItemSelected: (index: Int, item: String) -> Unit
) {

    Column(
        modifier = Modifier

    ) {
        var expanded by rememberSaveable {
            mutableStateOf(false)
        }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
            expanded = it
        }) {
            OutlinedTextField(
                value = list[selectedIndex].alias,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .width(270.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                modifier = Modifier.wrapContentHeight(),
                onDismissRequest = { expanded = false }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .width(270.dp)
                ) {
                    items(list.size) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            ) {
                                onItemSelected.invoke(it, list[it].alias)
                                expanded = false
                            }) {
                            Spacer(height = 10.dp)
                            Text(text = list[it].alias, modifier = Modifier.padding(start = 20.dp))
                            Spacer(height = 10.dp)
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }

}