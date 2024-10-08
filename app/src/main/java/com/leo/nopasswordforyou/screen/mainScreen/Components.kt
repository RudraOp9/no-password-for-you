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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.idapgroup.snowfall.snowmelt
import com.idapgroup.snowfall.types.FlakeType
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.NavigationDrawerItem
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.navigation.Screens
import com.leo.nopasswordforyou.viewmodel.MainActivityVm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DrawerContent(navCtrl: NavController, vm: MainActivityVm, context: Context) {
    val darkMode = isSystemInDarkTheme()
    ModalDrawerSheet(
        windowInsets = DrawerDefaults.windowInsets,
        drawerContainerColor = colorScheme.surface,
        modifier = Modifier.width(260.dp)
    ) {
        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .size(width = 260.dp, height = 70.dp)
                        .background(
                            colorScheme.primaryContainer,
                            RoundedCornerShape(20)
                        ),
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                    ) {
                        Text(
                            text = "No password for you",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = vm.themeIcon.collectAsState().value),
                                contentDescription = "Change theme",
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = null
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "Changing theme...",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        vm.toggleTheme(context, darkMode) {
                                            val activity = context as Activity
                                            activity.recreate()
                                        }
                                    }
                            )
                        }
                    }


                }
            }
            items(vm.drawerIcons.size) {
                NavigationDrawerItem(
                    label = { Text(text = vm.drawerText[it]) },
                    icon = {
                        Icon(
                            painter = painterResource(id = vm.drawerIcons[it]),
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        when (it) {
                            0 -> {
                                navCtrl.navigate(route = Screens.Dashboard.route)
                            }

                            1 -> {
                                navCtrl.navigate(route = Screens.Import.route)
                            }

                            2 -> {
                                navCtrl.navigate(route = Screens.Export.route)
                            }

                            3 -> {
                                navCtrl.navigate(route = Screens.Help.route)
                            }

                            4 -> {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://rudraop9.github.io/no-password-for-you/home/faq/")
                                )
                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "Open in browser"
                                    )
                                )
                            }

                            5 -> {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("mailto:work.ieo@outlook.com")
                                )
                                context.startActivity(Intent.createChooser(intent, "Mail in"))

                            }

                            6 -> {
                                //no plan as there are no server load
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("")
                                )
                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "Open in browser"
                                    )
                                )

                            }
                        }

                    }
                )
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPass(
    id: String,
    password: String,
    description: String,
    dismissDialog: () -> Unit,
    copy: (String) -> Unit,
    edit: () -> Unit
) {


    val coroutine = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            dismissDialog.invoke()
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            if (id.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Id : ", fontSize = typography.labelMedium.fontSize)
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var copyPassIcon by rememberSaveable {
                            mutableIntStateOf(R.drawable.icon_copy_24)
                        }

                        Icon(
                            painter = painterResource(id = copyPassIcon),
                            contentDescription = "",
                            modifier = Modifier
                                .sizeIn(maxWidth = 18.dp, maxHeight = 18.dp)
                                .clickable(
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    },
                                    indication = null
                                ) {
                                    copy.invoke(id)
                                    coroutine.launch {
                                        copyPassIcon = R.drawable.icon_done_24
                                        delay(1500)
                                        copyPassIcon = R.drawable.icon_copy_24
                                    }
                                }
                        )

                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                ) {

                    SpoilerEff {
                        Text(
                            text = id,
                            modifier = Modifier.fillMaxWidth()
                        )

                    }


                }
            }

            Spacer(height = 20.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "password : ", fontSize = typography.labelMedium.fontSize)
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var copyPassIcon by rememberSaveable {
                        mutableIntStateOf(R.drawable.icon_copy_24)
                    }

                    Icon(
                        painter = painterResource(
                            id = copyPassIcon
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .sizeIn(maxWidth = 18.dp, maxHeight = 18.dp)
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            ) {
                                copy.invoke(password)
                                coroutine.launch {
                                    copyPassIcon = R.drawable.icon_done_24
                                    delay(1500)
                                    copyPassIcon = R.drawable.icon_copy_24
                                }

                            }
                    )

                }


            }

            Spacer(height = 10.dp)
            SpoilerEff {
                Text(
                    text = password,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(height = 20.dp)

            if (description.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = description
                    )
                }
            }




            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {

                Button(
                    onClick = { dismissDialog.invoke() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = colorScheme.secondary,
                        contentColor = colorScheme.onSecondary
                    )
                ) {
                    Text(text = "Close")
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 20.dp))
                Button(
                    onClick = { edit.invoke() },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "Edit"
                    )
                }


            }
        }
    }


}

@Composable
fun SpoilerEff(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showSpoiler by rememberSaveable {
        mutableStateOf(true)
    }

    Box(modifier = modifier
        .height(IntrinsicSize.Min)
        .clickable(interactionSource = remember {
            MutableInteractionSource()
        }, indication = null, enabled = true) { showSpoiler = !showSpoiler }) {

        content()

        if (showSpoiler) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .width(IntrinsicSize.Min)

                    .snowmelt(
                        FlakeType.Custom(
                            listOf(
                                rememberVectorPainter(
                                    image = ImageVector.vectorResource(
                                        id = R.drawable.icon_spoiler1
                                    )
                                ),
                            )
                        ), density = 1.0
                    ),
                color = colorScheme.surfaceContainerHigh,
            ) {}
        }

    }
}