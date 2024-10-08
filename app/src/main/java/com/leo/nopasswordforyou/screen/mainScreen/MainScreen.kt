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

package com.leo.nopasswordforyou.screen.mainScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity.CLIPBOARD_SERVICE
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.R.string
import com.leo.nopasswordforyou.components.SortOptions
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.components.Vault
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.navigation.Screens
import com.leo.nopasswordforyou.screen.mainScreen.components.CreateNewItem
import com.leo.nopasswordforyou.screen.mainScreen.components.GenPass
import com.leo.nopasswordforyou.secuirity.Security
import com.leo.nopasswordforyou.util.sorting.sortPasswords
import com.leo.nopasswordforyou.viewmodel.MainActivityVm
import com.leo.nopasswordforyou.viewmodel.baseVM.AliasVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassListVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassesVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class
)

@Composable
fun MainScreen(
    navCtrl: NavController,
    vm: MainActivityVm = hiltViewModel(),
    aliasVm: AliasVM,
    passListVM: PassListVM,
    passesVM: PassesVM
) {
    val context = LocalContext.current
    vm.adjustTheme(context, isSystemInDarkTheme())
    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    val coroutine = rememberCoroutineScope()
    val snackBar = remember {
        SnackbarHostState()
    }

    LifecycleResumeEffect(key1 = Unit) {
        onPauseOrDispose {
            vm.authenticated = false
        }
    }/*
        WindowInsets.systemBarsForVisualComponents
            .only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)*/

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BackHandler(enabled = vm.startSelection.collectAsState().value || drawerState.isOpen) {
        if (drawerState.isOpen) {
            coroutine.launch {
                drawerState.close()
            }
        } else if (vm.startSelection.value) {
            vm.startSelection.value = false
            vm.selected.value = emptySet()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        ModalNavigationDrawer(
            modifier = Modifier, drawerState = drawerState, drawerContent = {
                DrawerContent(navCtrl = navCtrl, vm = vm, context)
            }, gesturesEnabled = true
        ) {//Navigation Drawer
            Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
                SnackbarHost(hostState = snackBar, modifier = Modifier.navigationBarsPadding())
            }, topBar = {
                TopAppBar(title = {
                    Text(
                        overflow = TextOverflow.Visible,
                        fontFamily = FontFamily.Serif,
                        text = stringResource(id = string.app_name),
                        fontWeight = FontWeight.Medium,
                        fontSize = typography.bodyLarge.fontSize,
                        color = colorScheme.onSurface
                    )
                }, navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                coroutine.launch {
                                    drawerState.open()
                                }
                            },
                        imageVector = (Icons.Default.Menu),
                        contentDescription = stringResource(id = string.click_to_open_drawer)
                    )
                }, actions = {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AnimatedVisibility(visible = (selectedTab == 1) && !vm.startSelection.collectAsState().value) {
                            IconButton(onClick = {
                                vm.sortDialog.value = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_sort),
                                    contentDescription = stringResource(id = string.sort_vault)
                                )
                            }
                        }


                        if (!vm.isLoggedIn() || !vm.isMailVerified()) {
                            Text(fontSize = typography.labelLarge.fontSize,
                                text = stringResource(id = string.login),
                                fontFamily = FontFamily.SansSerif,
                                color = colorScheme.primary,
                                modifier = Modifier.clickable(interactionSource = remember {
                                    MutableInteractionSource()
                                }, indication = null) {
                                    navCtrl.navigate(route = Screens.Login.route)
                                })
                        }
                        androidx.compose.animation.AnimatedVisibility(visible = selectedTab == 1) {
                            var showMenu by rememberSaveable {
                                mutableStateOf(false)
                            }
                            IconButton(onClick = {
                                showMenu = true
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = stringResource(id = string.vault_menu_option)
                                )
                            }

                            androidx.compose.animation.AnimatedVisibility(
                                visible = showMenu,
                                enter = fadeIn(tween(500)) + expandHorizontally(tween(500)),
                                exit = fadeOut(tween(100)) + shrinkHorizontally(tween(100)),


                                ) {
                                DropdownMenu(expanded = true, onDismissRequest = {
                                    showMenu = false
                                }) {

                                    DropdownMenuItem(text = {
                                        Text(
                                            fontFamily = FontFamily.SansSerif,
                                            text = stringResource(id = string.refresh)
                                        )
                                    }, onClick = {
                                        showMenu = false
                                        coroutine.launch(Dispatchers.Default) {
                                            vm.startWorking()
                                            passListVM.getPassesVault(
                                                vm.sortOrder, vm.sortBy
                                            ) {
                                                vm.stopWorking()
                                            }
                                            snackBar.showSnackbar("List refreshed")
                                        }
                                    })
                                    DropdownMenuItem(enabled = passListVM.passwordsVault.collectAsState().value.isNotEmpty(),
                                        text = {
                                            Text(
                                                fontFamily = FontFamily.SansSerif,
                                                text = stringResource(id = string.select)
                                            )
                                        },
                                        onClick = {
                                            vm.startSelection.value = true
                                            showMenu = false
                                        })
                                    DropdownMenuItem(enabled = vm.startSelection.collectAsState().value,
                                        text = {
                                            Text(
                                                fontFamily = FontFamily.SansSerif,
                                                text = stringResource(id = string.delete)
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            vm.showDeleteDialog.value = true
                                        })
                                }
                            }

                        }

                    }
                })
            }) { // Scaffold
                var width by rememberSaveable {
                    mutableIntStateOf(0)
                }

                val pagerState = rememberPagerState {
                    2
                }

                var showPassItem by rememberSaveable {
                    mutableStateOf(false)
                }

                var createNewPass by rememberSaveable {
                    mutableStateOf(false)
                }
                Column(modifier = Modifier.padding(it)) {
                    SecondaryTabRow(modifier = Modifier.padding(horizontal = 16.dp),
                        selectedTabIndex = 0,
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(modifier = Modifier
                                .height(2.dp)
                                .onGloballyPositioned { globalPos ->
                                    width = globalPos.size.width
                                }
                                .offset {
                                    IntOffset(
                                        x = (((-pagerState.getOffsetDistanceInPages(0)) * width).toInt()),
                                        y = 0
                                    )
                                }

                            )
                        }) {

                        Tab(modifier = Modifier.clip(RoundedCornerShape(12)),
                            selected = selectedTab == 0,
                            onClick = {
                                //     offset0 = 0f
                                selectedTab = 0
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 10.dp),
                                fontFamily = FontFamily.SansSerif,
                                text = stringResource(id = string.generate),
                                fontSize = typography.titleSmall.fontSize,
                                fontWeight = if (selectedTab == 1) typography.headlineSmall.fontWeight else typography.titleMedium.fontWeight
                            )
                        }

                        Tab(

                            modifier = Modifier.clip(RoundedCornerShape(12)),
                            selected = selectedTab == 1,
                            onClick = {
                                //     offset0 = 0.5f
                                selectedTab = 1
                            }) {
                            Text(
                                text = stringResource(id = string.vault),
                                fontSize = typography.titleSmall.fontSize,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = if (selectedTab == 0) typography.headlineSmall.fontWeight else typography.titleMedium.fontWeight
                            )
                        }
                    }

                    LaunchedEffect(key1 = selectedTab) {
                        pagerState.animateScrollToPage(
                            page = selectedTab, animationSpec = tween(easing = FastOutSlowInEasing)
                        )
                        if (selectedTab == 0) {
                            vm.startSelection.value = false
                            vm.selected.value = emptySet()
                        }
                    }

                    LaunchedEffect(key1 = pagerState.isScrollInProgress) {
                        if (!pagerState.isScrollInProgress) selectedTab = pagerState.currentPage
                    }
                    Spacer(height = 10.dp)
                    HorizontalPager(
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState, snapAnimationSpec = tween(
                                durationMillis = 250, easing = FastOutSlowInEasing
                            ), snapPositionalThreshold = 0.05f
                        ),
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = true,
                        verticalAlignment = Alignment.Top,
                        pageSpacing = 20.dp,
                        beyondViewportPageCount = 1,
                        state = pagerState,
                    ) { page ->
                        when (page) {
                            0 -> {

                                GenPass(vm,
                                    generatePass = { capAlpha, smallAlpha, numbers, symbols ->
                                        vm.generateNewPass(capAlpha, smallAlpha, numbers, symbols)
                                    }, setPass = { pass ->
                                        vm.passWord = pass
                                    }) {
                                    if (aliasVm.aliases.value.isEmpty()) {
                                        coroutine.launch {
                                            val result = snackBar.showSnackbar(
                                                message = "No Keys Found",
                                                actionLabel = "Create",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                navCtrl.navigate(Screens.Dashboard.route)
                                            }
                                        }

                                    } else {
                                        createNewPass = true

                                    }
                                }
                            }

                            //vault
                            1 -> {
                                Column(
                                    modifier = Modifier.padding(horizontal = 26.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AnimatedVisibility(vm.startSelection.collectAsState().value) {
                                        Row(
                                            modifier = Modifier.padding(top = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(fontFamily = FontFamily.SansSerif,
                                                text = if (vm.selectAll.collectAsState().value || vm.selected.collectAsState().value.size == passListVM.passwordsVault.collectAsState().value.size

                                                ) stringResource(id = string.unselect_all)
                                                else stringResource(id = string.select_all),

                                                color = if (vm.selectAll.collectAsState().value || vm.selected.collectAsState().value.size == passListVM.passwordsVault.collectAsState().value.size) colorScheme.error else colorScheme.primary,
                                                modifier = Modifier.clickable(
                                                    interactionSource = remember {
                                                        MutableInteractionSource()
                                                    }, indication = null
                                                ) {
                                                    vm.selectAll.value = !vm.selectAll.value
                                                    if (vm.selectAll.value) {
                                                        vm.selected.value =
                                                            emptySet<Int>().toMutableSet()
                                                                .apply {
                                                                    for (index in passListVM.passwordsVault.value.indices) {
                                                                        this.add(index)
                                                                    }
                                                                }
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
                                                Text(fontFamily = FontFamily.SansSerif,
                                                    text = stringResource(id = string.cancel),
                                                    modifier = Modifier.clickable(
                                                        remember {
                                                            MutableInteractionSource()
                                                        }, indication = null
                                                    ) {
                                                        vm.startSelection.value = false
                                                        vm.selectAll.value = false
                                                        vm.selected.value = emptySet()
                                                    })
                                            }
                                        }
                                    }
                                    Vault(
                                        selection = vm.startSelection.collectAsState().value,
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
                                        onItemClick = { index ->
                                            vm.startWorking()
                                            vm.decryptWithAuth(context,
                                                passListVM.passwordsVault.value[index],
                                                {
                                                    this.async {
                                                        passesVM.getPass(it).await()
                                                    }
                                                },
                                                { msg ->
                                                    coroutine.launch {
                                                        snackBar.showSnackbar(msg)
                                                    }
                                                }) {
                                                showPassItem = true
                                                vm.stopWorking()
                                            }
                                        },
                                        items = passListVM.passwordsVault.collectAsState()
                                    )
                                }
                            }

                        }

                    }



                    if (showPassItem) {
                        var updateItem by rememberSaveable {
                            mutableStateOf(false)
                        }
                        if (vm.decodedData != null) {
                            ShowPass(id = vm.userIdDecrypted,
                                password = vm.decodedData ?: "Error getting password",
                                description = vm.desc,
                                dismissDialog = { showPassItem = false },
                                copy = { text ->
                                    vm.copy(context, text)
                                    val clipboard =
                                        context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Copied Text", text)
                                    clipboard.setPrimaryClip(clip)
                                }) {
                                updateItem = true
                            }
                        }
                        if (updateItem) {

                            if (vm.decodedData != null) {
                                CreateNewItem(arrayKeys = null,
                                    alias0 = vm.aliasStr,
                                    title0 = vm.titleDecrypted,
                                    uid0 = vm.userIdDecrypted,
                                    desc0 = vm.desc,
                                    password0 = vm.decodedData ?: "",
                                    isUpdate = true,
                                    vm = vm,
                                    onExit = {
                                        updateItem = false
                                    }) { title, uid, desc, alias, _, pass, uploadOnCloud ->
                                    showPassItem = false


                                    if (title.isEmpty()) {
                                        Toast.makeText(context, "Empty title", Toast.LENGTH_SHORT)
                                            .show()
                                        updateItem = true
                                    } else {
                                        vm.startWorking()
                                        updateItem = false
                                        coroutine.launch(Dispatchers.Default) {
                                            val security1 = Security(context)
                                            val encNewPass = security1.encryptData(pass, alias) {
                                                coroutine.launch {
                                                    snackBar.showSnackbar(it)
                                                    updateItem = true
                                                }
                                            }
                                            if (encNewPass != null) {

                                                if (uploadOnCloud) {
                                                    vm.putPassCloud(
                                                        encPass = encNewPass,
                                                        passTitle = title,
                                                        passDesc = desc,
                                                        passUserId = uid,
                                                        context = context,
                                                        passId = vm.passIdDecrypted,
                                                        modify = System.currentTimeMillis(),
                                                        alias = alias
                                                    ) { code, msg ->
                                                        if (code == 0) {
                                                            passListVM.updatePassList(
                                                                PassListEntity(
                                                                    passId = vm.passIdDecrypted,
                                                                    title = title,
                                                                    desc = desc,
                                                                    alias = alias,
                                                                    lastModify = System.currentTimeMillis(),
                                                                    onCloud = true
                                                                )
                                                            ) {
                                                                passesVM.updatePass(
                                                                    PassesEntity(
                                                                        passId = vm.passIdDecrypted,
                                                                        userId = uid,
                                                                        password = encNewPass,
                                                                        alias = alias
                                                                    )
                                                                ) {
                                                                    passListVM.getPassesVault(
                                                                        vm.sortOrder,
                                                                        vm.sortBy
                                                                    ) {
                                                                        vm.stopWorking()
                                                                    }

                                                                }
                                                            }


                                                        }
                                                    }
                                                } else {
                                                    passListVM.updatePassList(
                                                        PassListEntity(
                                                            passId = vm.passIdDecrypted,
                                                            title = title,
                                                            desc = desc,
                                                            alias = alias,
                                                            lastModify = System.currentTimeMillis(),
                                                            onCloud = false
                                                        )
                                                    ) {
                                                        passesVM.updatePass(
                                                            PassesEntity(
                                                                passId = vm.passIdDecrypted,
                                                                userId = uid,
                                                                password = encNewPass,
                                                                alias = alias
                                                            )
                                                        ) {
                                                            passListVM.getPassesVault(
                                                                vm.sortOrder,
                                                                vm.sortBy
                                                            ) {
                                                                vm.stopWorking()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (createNewPass) {
                        CreateNewItem(title0 = "",
                            uid0 = "",
                            desc0 = "",
                            alias0 = "",
                            password0 = vm.passWord,
                            arrayKeys = aliasVm.aliases.value,
                            vm = vm,
                            onExit = {
                                createNewPass = false
                            }) { title, uid, desc, alias, keySelectedIndex, pass, uploadOnCloud ->
                            createNewPass = false
                            vm.startWorking()
                            if (title.isNotEmpty()) {
                                vm.createNewPass(title,
                                    uid,
                                    desc,
                                    alias,
                                    keySelectedIndex,
                                    pass,
                                    uploadOnCloud,
                                    context,
                                    { msg ->
                                        coroutine.launch {
                                            snackBar.showSnackbar(
                                                msg,
                                                duration = SnackbarDuration.Short
                                            )
                                            vm.stopWorking()
                                            createNewPass = true
                                        }

                                    }) { encPass, passId, modify, onCloud ->

                                    coroutine.launch {

                                        passListVM.putPassDevice(
                                            passId = passId,
                                            passTitle = title,
                                            passDesc = desc,
                                            modify = modify,
                                            onCloud = onCloud,
                                            alias = alias
                                        ) {
                                            passesVM.putPasses(
                                                passId = passId,
                                                userId = uid,
                                                password = encPass,
                                                alias = alias
                                            )
                                        }
                                        passListVM.getPassesVault(vm.sortOrder, vm.sortBy) {
                                            vm.stopWorking()
                                        }
                                        snackBar.showSnackbar("Successfully added")
                                    }
                                }

                            } else {
                                Toast.makeText(context, "Empty title", Toast.LENGTH_SHORT).show()
                                vm.stopWorking()
                                createNewPass = true
                            }
                        }

                    }
                }
            }
        }
        if (vm.showDeleteDialog.collectAsState().value) {
            AlertDialog(onDismissRequest = { }, text = {
                Text(
                    fontFamily = FontFamily.SansSerif,
                    text = stringResource(id = string.delete_selected_dialog)
                )
            }, confirmButton = {
                Button(colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = Color.Transparent, contentColor = colorScheme.error),
                    onClick = {
                        vm.startWorking()
                        vm.showDeleteDialog.value = false
                        vm.deleteSelected(passListVM.passwordsVault.value, deleteFromDevice = {
                            coroutine.launch(Dispatchers.Default) {
                                passListVM.deleteFromDevice(vm.selected.value.toList(), {
                                    passesVM.deleteFromDevice(it)
                                }) {
                                    coroutine.launch {
                                        passListVM.getPassesVault(vm.sortOrder, vm.sortBy) {
                                            vm.stopWorking()
                                        }
                                        vm.selected.value = emptySet()
                                        vm.startSelection.value = false
                                        vm.selectAll.value = false
                                    }
                                }



                                vm.stopWorking()
                            }
                        }) { msg ->
                            coroutine.launch {
                                snackBar.showSnackbar(msg)
                            }
                        }
                    }) {
                    Text(text = stringResource(id = string.yes))
                }
            }, dismissButton = {
                Button(colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Transparent,
                    contentColor = AlertDialogDefaults.textContentColor
                ), onClick = {
                    vm.showDeleteDialog.value = false
                }) {
                    Text(text = stringResource(id = string.cancel))
                }
            })
        }

        if (vm.showCloudDel.collectAsState().value) {
            AlertDialog(onDismissRequest = { }, text = {
                Text(
                    fontFamily = FontFamily.SansSerif,
                    text = stringResource(id = string.delete_selected_cloud_dialog)
                )
            }, confirmButton = {
                Button(colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = Color.Transparent, contentColor = colorScheme.error),
                    onClick = {
                        coroutine.launch {
                            vm.userInputChannel.send(true)
                            vm.showCloudDel.value = false
                        }
                    }) {
                    Text(text = stringResource(id = string.yes))
                }
            }, dismissButton = {
                Button(colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Transparent,
                    contentColor = AlertDialogDefaults.textContentColor
                ), onClick = {
                    coroutine.launch {
                        vm.userInputChannel.send(false)
                        vm.showCloudDel.value = false
                    }
                }) {
                    Text(text = stringResource(id = string.no))
                }
            })
        }
        if (vm.working.value) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {},
                color = colorScheme.scrim.copy(0.4f)
            ) {}
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (vm.sortDialog.collectAsState().value) {
            SortOptions(selected = vm.sortBy, sortOrder = vm.sortOrder, onDialogueDismiss = {
                vm.sortDialog.value = false
            }) { sort, sortOrder ->
                vm.sortOrder = sortOrder
                vm.sortBy = sort
                coroutine.launch(Dispatchers.Main) {
                    vm.sortDialog.value = false
                    vm.startWorking()
                    launch(Dispatchers.Default) {
                        sortPasswords(passListVM.passwordsVault.value, sortOrder, sort) {
                            passListVM.passwordsVault.value = it
                            vm.stopWorking()
                        }
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSet(
    title: String,
    value: Int,
    checkBox: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    list: List<Int> = (1..15).toList(),
    onItemSelected: (index: Int, value: Int) -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = it
    }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Checkbox(checked = checkBox, onCheckedChange = onCheckChanged)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
            ) {
                OutlinedTextField(
                    value = title,
                    trailingIcon = {
                        Row {
                            Text(
                                text = "$value",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = typography.titleMedium.fontWeight,
                                fontSize = typography.titleMedium.fontSize,
                                color = colorScheme.onSurface
                            )
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    },
                    onValueChange = { },
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )
            }
            ExposedDropdownMenu(expanded = expanded,
                modifier = Modifier.wrapContentHeight(),
                onDismissRequest = { expanded = false }) {
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
                                }, indication = null
                            ) {
                                onItemSelected.invoke(it, list[it])
                                expanded = false
                            }) {
                            Spacer(height = 10.dp)
                            Text(
                                fontFamily = FontFamily.SansSerif,
                                text = "${list[it]}",
                                modifier = Modifier.padding(start = 20.dp)
                            )
                            Spacer(height = 10.dp)
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

        }

    }

}










