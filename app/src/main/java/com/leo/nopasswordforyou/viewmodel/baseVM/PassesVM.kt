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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passes.PassesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PassesVM @Inject constructor(
    private val passesDao: PassesDao
) : ViewModel() {
    suspend fun getPass(keyId: String): Deferred<PassesEntity> {
        return withContext(Dispatchers.IO) {
            async {
                passesDao.getPass(keyId) ?: PassesEntity()
            }
        }
    }


    fun putPasses(passId: String, userId: String, password: String, alias: String) {
        viewModelScope.launch(Dispatchers.IO) {
            passesDao.insertNewPass(
                PassesEntity(
                    passId = passId,
                    userId = userId,
                    password = password,
                    alias = alias
                )
            )

        }
    }

    fun putPasses(passes: List<PassesEntity>, success: () -> Unit) {
        viewModelScope.launch {
            for (i in passes.indices) {
                if (passesDao.getPass(passes[i].passId)?.passId != passes[i].passId) {
                    passesDao.insertNewPass(passes[i])
                }
            }
            success()
            //passesDao.insertAllPass(passes)

        }
    }

    fun updatePass(passesEntity: PassesEntity, success: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            passesDao.updatePass(passesEntity)
            success()
        }
    }

    fun deleteFromDevice(passId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            passesDao.deletePass(passId)
        }
    }
}



