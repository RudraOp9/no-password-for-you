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

package com.leo.nopasswordforyou.viewmodel.baseVM

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.alias.AliasEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AliasVM @Inject constructor(
    private val aliasDao: AliasDao
) : ViewModel() {
    var aliases = mutableStateOf(emptyList<AliasEntity>())

    init {
        viewModelScope.launch(Dispatchers.Default) {
            getAliases {}

        }
    }

    fun containsAlias(alias: String): Boolean {
        aliases.value.forEach {
            if (it.alias == alias) {
                return true
            }
        }
        return false
    }

    fun deleteAlias(aliasData: AliasEntity, success: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "deleteAlias: Deleting an alias ${aliasData.alias}")
            if (containsAlias(aliasData.alias)) {
                aliasDao.deleteAlias(aliasData)
                success.invoke()
            } else {
                success.invoke()
            }
        }
    }

    fun getAliases(success: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            aliases.value = aliasDao.getAllAlias()
            success()
            Log.d("TAG", "getAliases: Finding new aliases ${aliases.value}")
        }
    }

    fun setAlias(alias: String, success: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "setAlias: inserting new alias $alias")
            aliasDao.insertNewAlias(
                AliasEntity(
                    alias = alias, date = SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                    ).format(Date())
                )
            )
            success()
        }
    }


}