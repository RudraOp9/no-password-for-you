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

package com.leo.nopasswordforyou.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.leo.nopasswordforyou.ui.theme.Github
import com.leo.nopasswordforyou.ui.theme.Mail
import com.leo.nopasswordforyou.ui.theme.Telegram

class HelpVM : ViewModel() {
    fun openHelpPage(index: Int, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        when (index) {
            0 -> {
                intent.data = "https://tx.me/no_password_for_you".toUri()
            }

            1 -> {
                intent.data = "https://github.com/RudraOp9/no-password-for-you".toUri()
            }

            2 -> {
                intent.data = "mailto:work.ieo@outlook.com".toUri()
            }
        }
        context.startActivity(Intent.createChooser(intent, "Open in"))
    }

    val icons = listOf("icon_tg_logo.webp", "icon_github_blue.webp", "icon_mail.webp")
    val colors = listOf(Telegram, Github, Mail)
    val title = listOf("Telegram", "Github", "Mail")
    val desc = listOf("Ask the Telegram community", "Report a bug on GitHub", "Email Us")
}