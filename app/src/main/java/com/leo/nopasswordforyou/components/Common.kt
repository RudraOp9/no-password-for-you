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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun SelectRadio(selected: Boolean, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = remember {
                MutableInteractionSource()
            },
            indication = null
        ) {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick)
        Spacer(width = 10.dp)
        Text(text = text, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun Spacer(modifier: Modifier = Modifier, height: Dp = 0.dp, width: Dp = 0.dp) {
    androidx.compose.foundation.layout.Spacer(
        modifier = modifier
            .height(height)
            .width(width)
    )

}

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.safeRequireOffset(): Float {
    return try {
        requireOffset()
    } catch (e: Exception) {
        0.0f
    }
}