/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 20/05/24, 10:03 pm
 *  Copyright (c) 2024 . All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.leo.nopasswordforyou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.alias.AliasEntity
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListDao
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShowPassVM @Inject constructor(
    val passesDao: PassesDao,
    val passListDao: PassListDao,
    val aliasDao: AliasDao
) :
    ViewModel() {
    fun getPassList(results: (ArrayList<PassListEntity>) -> Unit) {
        viewModelScope.launch {
            val k: ArrayList<PassListEntity> =
                passListDao.getPassList() as ArrayList<PassListEntity>
            results.invoke(k)
        }
    }

    fun setAlias(alias: String) {
        viewModelScope.launch {
            aliasDao.insertNewAlias(AliasEntity(alias = alias))
        }
    }


    fun getPass(keyId: String, results: (PassesEntity) -> Unit) {
        viewModelScope.launch {
            results.invoke(passesDao.getPass(keyId))
        }
    }


    fun updatePass(passesEntity: PassesEntity) {
        viewModelScope.launch {
            passesDao.updatePass(passesEntity)
        }
    }

    fun deletePass(keyId: String) {
        viewModelScope.launch {
            passesDao.deletePass(keyId)
        }

    }

    fun deletePassList(keyId: String) {
        viewModelScope.launch {
            passListDao.deletePassList(keyId)
        }
    }

    fun updatePassList(passListEntity: PassListEntity) {
        viewModelScope.launch {
            passListDao.updatePassList(passListEntity)
        }
    }




}