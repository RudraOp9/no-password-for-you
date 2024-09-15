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

package com.leo.nopasswordforyou.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leo.nopasswordforyou.screen.keyManageScreen.DashBoard
import com.leo.nopasswordforyou.screen.ExportScreen
import com.leo.nopasswordforyou.screen.HelpScreen
import com.leo.nopasswordforyou.screen.ImportScreen
import com.leo.nopasswordforyou.screen.Login
import com.leo.nopasswordforyou.screen.SignUp
import com.leo.nopasswordforyou.screen.mainScreen.MainScreen
import com.leo.nopasswordforyou.viewmodel.baseVM.AliasVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassListVM
import com.leo.nopasswordforyou.viewmodel.baseVM.PassesVM

@Composable
fun Navigation() {
    val navCtrl = rememberNavController()
    val aliasVM = hiltViewModel<AliasVM>()
    val passListVM = hiltViewModel<PassListVM>()
    val passesVM = hiltViewModel<PassesVM>()
    NavHost(
        navController = navCtrl,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    400, easing = FastOutSlowInEasing
                )
            ) + slideIntoContainer(
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    300, easing = FastOutSlowInEasing
                )
            ) + slideOutOfContainer(
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        },
        startDestination = Screens.Home.route
    ) {
        composable(Screens.Home.route, enterTransition = { EnterTransition.None }) {
            MainScreen(navCtrl, aliasVm = aliasVM, passListVM = passListVM, passesVM = passesVM)
        }
        composable(Screens.SignUp.route) {
            SignUp(navCtrl)
        }
        composable(Screens.Login.route) {
            Login(navCtrl)
        }
        composable(Screens.Dashboard.route) {
            DashBoard(aliasVm = aliasVM)
        }
        composable(Screens.Export.route) {
            ExportScreen(passesVM = passesVM, passListVM = passListVM)
        }
        composable(Screens.Import.route) {
            ImportScreen(passesVM = passesVM, passListVM = passListVM)
        }
        composable(Screens.Help.route) {
            HelpScreen()
        }
    }
}