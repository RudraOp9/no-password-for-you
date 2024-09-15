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

package com.leo.nopasswordforyou.screen.keyManageScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.screen.keyManageScreen.components.NewKeyDialogue
import com.leo.nopasswordforyou.viewmodel.KeyManageVM
import com.leo.nopasswordforyou.viewmodel.baseVM.AliasVM
import kotlinx.coroutines.launch

@Composable
fun DashBoard(vm: KeyManageVM = hiltViewModel<KeyManageVM>(), aliasVm: AliasVM) { //Dashboard


    val context = LocalContext.current

    val snackBar = remember {
        SnackbarHostState()
    }
    val coroutine = rememberCoroutineScope()

    vm.SelectAndReadFile(
        message = {
            coroutine.launch {
                if (snackBar.currentSnackbarData != null) {
                    snackBar.currentSnackbarData?.dismiss()
                }
                snackBar.showSnackbar(it)
            }
        },
        uploadAlias = { alias ->
            coroutine.launch {
                aliasVm.setAlias(alias) {
                    aliasVm.getAliases {
                        vm.working.value = false

                    }
                }

            }


        },
    ) {
        vm.working.value = it
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()

    ) {//Box
        Surface(
            onClick = { /*TODO*/ },
            enabled = false,
            color = colorScheme.surface,
            modifier = Modifier.fillMaxSize()
        ) { //Surface
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            ) {

                LazyColumn(modifier = Modifier) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Spacer(height = 30.dp)

                            Row(
                                modifier = Modifier, verticalAlignment = Alignment.CenterVertically
                            ) {
                                HeadInfoText(title = "KEYS")
                                Spacer(width = 10.dp)
                                HorizontalDivider()
                            }


                            Spacer(height = 20.dp)
                            PrimaryListStart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        remember { MutableInteractionSource() }, indication = null
                                    ) {
                                        vm.createNewKey.value = true
                                    },
                                title = "Create new key",
                                iconRes = R.drawable.icon_add,
                                background = colorScheme.primary,
                                tint = colorScheme.onPrimary
                            )
                            Spacer(height = 10.dp)
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                            Spacer(height = 10.dp)

                            PrimaryListStart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        remember { MutableInteractionSource() }, indication = null
                                    ) {
                                        vm.launchSelectFile()
                                    },
                                title = "Import key",
                                iconRes = R.drawable.icon_download,
                                background = colorScheme.primary,
                                tint = colorScheme.onPrimary
                            )


                            Spacer(height = 10.dp)
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                            Spacer(height = 10.dp)


                        }
                    }

                    items(aliasVm.aliases.value, key = { it.id }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = it.alias, fontSize = typography.titleMedium.fontSize
                                )
                                Spacer(height = 5.dp)
                                Text(
                                    text = it.date, fontSize = typography.labelSmall.fontSize
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .weight(0.2f)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(imageVector = Icons.TwoTone.Delete,
                                    contentDescription = "",
                                    tint = colorScheme.secondary,
                                    modifier = Modifier.clickable {

                                        coroutine.launch {
                                            vm.startWorking()
                                            vm.deleteKey(context, it.alias, {
                                                coroutine.launch {
                                                    snackBar.showSnackbar(it)
                                                    vm.stopWorking()

                                                }
                                            }) {
                                                coroutine.launch {
                                                    aliasVm.deleteAlias(it) {
                                                        aliasVm.getAliases {
                                                            vm.stopWorking()
                                                        }
                                                    }

                                                }

                                            }
                                        }
                                    })
                            }
                        }
                        Spacer(height = 10.dp)
                        HorizontalDivider(
                            color = DividerDefaults.color.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                    }
                }
            }
            Spacer(height = 20.dp)


        }

        if (vm.working.value) {
            Surface(
                modifier = Modifier.fillMaxSize(), color = colorScheme.background.copy(alpha = 0.4f)
            ) {

            }
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        SnackbarHost(
            hostState = snackBar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .navigationBarsPadding()
        )
    }

    NewKeyDialogue(visible = vm.createNewKey.value, onCreateKey = {
        vm.createNewKey.value = false
        vm.startWorking()
        coroutine.launch {
            vm.createKey(vm.createKeyAlias.value, context, checkAlias = {
                aliasVm.containsAlias(it)
            }, setAlias = {
                aliasVm.setAlias(it) {
                    aliasVm.getAliases {
                        vm.stopWorking()
                    }
                }
            }) { it1 ->
                coroutine.launch {
                    if (snackBar.currentSnackbarData != null) {
                        snackBar.currentSnackbarData?.dismiss()
                    }
                    snackBar.showSnackbar(it1)
                }
            }
        }

    }, key = vm.createKeyAlias) {
        vm.createNewKey.value = false
    }


}


@Composable
fun HeadInfoText(
    modifier: Modifier = Modifier, title: String, color: Color = typography.titleMedium.color
) {
    Text(
        modifier = modifier, text = title, fontSize = typography.titleSmall.fontSize, color = color
    )
}

@Composable
fun PrimaryListStart(
    modifier: Modifier = Modifier,
    title: String,
    iconRes: Int,
    tint: Color = LocalContentColor.current,
    background: Color = LocalContentColor.current
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    background, CircleShape
                )
                .size(40.dp)
                .wrapContentSize(),
            tint = tint

        )
        Spacer(width = 10.dp)
        Text(text = title, fontSize = typography.titleMedium.fontSize)
    }

}


