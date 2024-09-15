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

package com.leo.nopasswordforyou.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.components.Vault
import com.leo.nopasswordforyou.viewmodel.ExportVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassListVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassesVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    vm: ExportVM = hiltViewModel<ExportVM>(),
    passListVM: PassListVM,
    passesVM: PassesVM
) {
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = {
            Column {
                Text(
                    text = "Export vault",
                    fontSize = typography.titleMedium.fontSize,
                )
                Text(
                    text = "only vault stored in device will be exported.",
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = typography.labelMedium.fontSize,
                    color = colorScheme.outline
                )
            }
        }
        )

    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {//box
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()/*.padding(horizontal = 20.dp)*/
                ) {
                    HorizontalDivider()
                    Spacer(height = 10.dp)

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 26.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                    ) {

                        Text(text = if (
                            vm.selectAll.collectAsState().value ||
                            vm.selected.collectAsState().value.size == passListVM.passwordsVault.collectAsState().value.size
                        ) "Unselect all" else "Select all",

                            color = if (
                                vm.selectAll.collectAsState().value ||
                                vm.selected.collectAsState().value.size == passListVM.passwordsVault.collectAsState().value.size
                            ) colorScheme.error else colorScheme.primary,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    },
                                    indication = null,
                                    enabled = passListVM.passwordsVault.collectAsState().value.isNotEmpty()
                                ) {
                                    vm.selectAll.value = !vm.selectAll.value

                                    if (vm.selectAll.value) {
                                        vm.selected.value =
                                            passListVM.passwordsVault.value.indices.toSet()
                                    } else {
                                        vm.selected.value = emptySet()
                                    }


                                })
                        Spacer(width = 10.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                enabled = vm.selected.collectAsState().value.isNotEmpty(),
                                onClick = {
                                    vm.export(context, passListVM.passwordsVault.value) { key ->
                                        this.async(Dispatchers.IO) {
                                            passesVM.getPass(key).await()
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12)
                            ) {
                                Text(text = "Export")
                            }

                        }
                    }
                    Spacer(height = 10.dp)

                    HorizontalDivider()
                    Vault(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxSize(),
                        selection = true,
                        selected = vm.selected.collectAsState().value,
                        onSelectionChange = { it, it1 ->
                            if (it) {
                                vm.selected.value =
                                    vm.selected.value.toMutableSet().apply {
                                        this.remove(it1)
                                    }
                                vm.selectAll.value = false

                            } else {
                                vm.selected.value = vm.selected.value.toMutableSet()
                                    .apply { this.add(it1) }

                                if (vm.selected.value.size == passListVM.passwordsVault.value.size) {
                                    vm.selectAll.value = true
                                }

                            }
                        },
                        onItemHold = { item ->
                            vm.startSelection.value = true
                            vm.selected.value =
                                vm.selected.value.toMutableSet().apply {
                                    this.add(item)
                                }
                        },
                        onItemClick = {},
                        items = passListVM.passwordsVault.collectAsState()
                    )

                }

                if (vm.working.collectAsState().value) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = colorScheme.background.copy(alpha = 0.4f)
                    ) {

                    }
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }


        }
    }
}