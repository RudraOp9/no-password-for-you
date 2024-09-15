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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.screen.keyManageScreen.HeadInfoText
import com.leo.nopasswordforyou.util.sorting.SortOrder
import com.leo.nopasswordforyou.util.sorting.Sorting
import com.leo.nopasswordforyou.viewmodel.ImportVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassListVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassesVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    vm: ImportVM = hiltViewModel<ImportVM>(),
    passListVM: PassListVM,
    passesVM: PassesVM
) {

    val coroutine = rememberCoroutineScope()
    val snackBar = remember {
        SnackbarHostState()
    }
    vm.SelectAndReadFile(message = {
        coroutine.launch {
            snackBar.showSnackbar(it)
        }
    }) {
        vm.working.value = it
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Column {
                Text(
                    text = "Import vault",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            }
        }
        )

    }
    ) { // scaffold
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { // surface
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) { // box
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeadInfoText(title = "NO PASSWORD FOR YOU")
                        Spacer(width = 10.dp)
                        HorizontalDivider()
                    }
                    Spacer(height = 20.dp)
                    Button(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = {
                            vm.noPassImport.value = true
                        }, shape = RoundedCornerShape(12)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_download),
                            contentDescription = ""
                        )
                        Spacer(width = 10.dp)
                        Text(text = "Import from files")
                    }
                    Spacer(height = 20.dp)
                    Button(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = {
                            vm.cloudImportDialogue.value = true
                        }, shape = RoundedCornerShape(12)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_import_cloud),
                            contentDescription = ""
                        )
                        Spacer(width = 10.dp)
                        Text(text = "Import from Cloud")
                    }
                    Spacer(height = 20.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeadInfoText(title = "OTHERS")
                        Spacer(width = 10.dp)
                        HorizontalDivider()
                    }
                    Spacer(height = 10.dp)
                    Text(text = "Coming soon ...")
                }

                if (vm.noPassImport.value) {
                    ModalBottomSheet(onDismissRequest = {
                        vm.noPassImport.value = !vm.noPassImport.value
                    }) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedButton(
                                onClick = {
                                    vm.importMapping = true
                                    vm.importMappingOrPass()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12)
                            ) {
                                Text(text = vm.mappingText.value)
                            }
                            Spacer(height = 20.dp)
                            OutlinedButton(
                                onClick = {
                                    vm.importMapping = false
                                    vm.importMappingOrPass()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12)
                            ) {
                                Text(text = vm.passText.value)
                            }

                            Spacer(height = 20.dp)
                            Column(
                                modifier = Modifier,
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        vm.startImport(success = {
                                            coroutine.launch {
                                                vm.noPassImport.value = false
                                                snackBar.currentSnackbarData?.dismiss()
                                                snackBar.showSnackbar(message = "Successfully Imported")
                                            }
                                        }, putPasses = { passes, mapping ->
                                            coroutine.launch(Dispatchers.IO) {
                                                passListVM.putPassList(mapping) {
                                                    passListVM.getPassesVault(
                                                        SortOrder.Ascending,
                                                        Sorting.Title
                                                    ) {
                                                        passesVM.putPasses(passes) {
                                                            vm.working.value = false
                                                            vm.mappingText.value =
                                                                "Select mapping file"
                                                            vm.passText.value = "Select pass file"
                                                        }
                                                    }

                                                }
                                            }
                                        }) {
                                            coroutine.launch {
                                                snackBar.showSnackbar(it)
                                            }
                                        }
                                    }, shape = RoundedCornerShape(12)
                                ) {
                                    Text(text = "Done")
                                }
                            }
                        }

                    }

                }
                if (vm.cloudImportDialogue.value) {
                    AlertDialog(onDismissRequest = {
                        vm.cloudImportDialogue.value = false
                    }, text = {
                        Text(text = "Import all passwords from cloud ? any existing password will be skipped.")
                    }, confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    containerColor = Color.Transparent,
                                    contentColor = AlertDialogDefaults.textContentColor
                                ),
                            onClick = {
                                vm.working.value = true
                                vm.cloudImportDialogue.value = false

                                vm.importCloud(message = { msg ->
                                    coroutine.launch {
                                        snackBar.showSnackbar(msg)
                                    }
                                }, error = {
                                    vm.working.value = false
                                }, putPasses = { pass, map ->
                                    coroutine.launch(Dispatchers.IO) {
                                        passListVM.putPassList(map) {
                                            passesVM.putPasses(pass) {
                                                passListVM.getPassesVault(
                                                    SortOrder.Ascending,
                                                    Sorting.Title
                                                ) {
                                                    vm.working.value = false
                                                }

                                            }
                                        }

                                    }

                                }) {
                                }
                            }) {
                            Text(text = "Ok")
                        }
                    }, dismissButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    containerColor = Color.Transparent,
                                    contentColor = colorScheme.error
                                ),
                            onClick = {
                                vm.cloudImportDialogue.value = false
                            }) {
                            Text(text = "Cancel")
                        }
                    })
                }
                if (vm.working.value) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = colorScheme.background.copy(alpha = 0.4f)
                    ) {

                    }
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (vm.inconsistentError.value) {
                    AlertDialog(onDismissRequest = {
                        vm.inconsistentError.value = false
                    }, confirmButton = {
                        OutlinedButton(
                            onClick = {
                                vm.inconsistentError.value = false
                                coroutine.launch {
                                    vm.userInputChannel.send(true)
                                }
                            },
                            shape = RoundedCornerShape(12),
                            colors = ButtonDefaults.outlinedButtonColors()
                                .copy(containerColor = colorScheme.errorContainer)
                        ) {
                            Text(text = "Ok")
                        }
                    }, dismissButton = {
                        OutlinedButton(onClick = {
                            vm.inconsistentError.value = false
                            coroutine.launch {
                                vm.userInputChannel.send(false)
                            }
                        }, shape = RoundedCornerShape(12)) {
                            Text(text = "Cancel")
                        }

                    },
                        text = {
                            Text(text = "The Mapping and Pass file does not match , still import ?")
                        },
                        properties = DialogProperties(
                            dismissOnClickOutside = false,
                            dismissOnBackPress = false,
                        )
                    )
                }


            }
        }
    }
    Box(Modifier.systemBarsPadding()) {
        SnackbarHost(
            hostState = snackBar,
            modifier = Modifier
                .align(Alignment.TopCenter)

        )
    }

}