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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.viewmodel.HelpVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(vm: HelpVM = hiltViewModel<HelpVM>()) {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Help")
        })
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn {
                items(vm.icons.size) { index ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(height = 10.dp)
                        HelpCard(
                            icon = vm.icons[index],
                            title = vm.title[index],
                            desc = vm.desc[index],
                            mainColor = vm.colors[index]
                        ) {
                            vm.openHelpPage(index, context)

                        }
                        Spacer(height = 10.dp)
                        if (index != vm.icons.size - 1) {
                            HorizontalDivider(modifier = Modifier.width(360.dp))
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun HelpCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    desc: String,
    mainColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
            .background(
                Brush.linearGradient(
                    listOf(
                        colorScheme.surfaceContainerHighest,
                        mainColor
                    )
                ), RoundedCornerShape(12)
            )
            .width(360.dp)
            .height(80.dp)
            .clickable(remember {
                MutableInteractionSource()
            }, null) {
                onClick.invoke()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            //   AsyncImage(model = painterResource(passIdDecrypted = R.drawable.round_brightness_auto_24), contentDescription = )
            AsyncImage(
                model = "https://raw.githubusercontent.com/RudraOp9/no-password-for-you/master/res/$icon",
                contentDescription = desc,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        colorScheme.primary,
                        CircleShape
                    )
                    .wrapContentHeight(Alignment.CenterVertically),
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = typography.titleMedium.fontSize
                )
                Spacer(height = 5.dp)
                Text(
                    text = desc,
                    fontSize = typography.labelSmall.fontSize
                )
            }
        }
    }
}