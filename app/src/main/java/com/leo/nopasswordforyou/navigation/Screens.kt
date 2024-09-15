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

sealed class Screens(val route: String) {
    data object Home : Screens("home_screen")
    data object Dashboard : Screens("dashboard")
    data object Login : Screens("login")
    data object SignUp : Screens("sign_up")
    data object Export : Screens("export")
    data object Import : Screens("import")
    data object Help : Screens("help")


}