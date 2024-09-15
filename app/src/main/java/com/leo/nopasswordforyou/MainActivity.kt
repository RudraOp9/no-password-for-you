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

package com.leo.nopasswordforyou

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.fragment.app.FragmentActivity
import com.leo.nopasswordforyou.navigation.Navigation
import com.leo.nopasswordforyou.ui.theme.NoPasswordForYouTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sPref = getSharedPreferences("app", MODE_PRIVATE)
        val theme = sPref.getBoolean("Theme", false)
        val dark = sPref.getBoolean("dark", false)


        setContent {
            NoPasswordForYouTheme(if (theme) dark else isSystemInDarkTheme()) {
                Navigation()
            }
        }
    }
}

