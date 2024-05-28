/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 20/05/24, 10:02 pm
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

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListDao
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.helper.NewPass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeneratePassVM @Inject constructor(
    val aliasDao: AliasDao,
    val passesDao: PassesDao,
    val passListDao: PassListDao
) : ViewModel() {
    var alphaCapLength: Byte = 4
    var specialSymbol: Byte = 2
    var numberslen: Byte = 4
    var alphaSmallLength: Byte = 6
    var passLength: Byte = 16
    var newPass: NewPass
    var aliases = ArrayList<String>()
    var selectedAlias = 0


    var passWord: MutableLiveData<String> = MutableLiveData("")
    var total: MutableLiveData<String> = MutableLiveData("")

    init {
        passWord.value = ""
        newPass = NewPass();
        genNewPass()
    }

    fun genNewPass() {
        passWord.value = newPass.generateNewPass(
            alphaCapLength,
            specialSymbol,
            numberslen,
            alphaSmallLength,
            passLength
        )
    }

    fun updateTotalText() {
        total.value = (alphaCapLength + alphaSmallLength + specialSymbol + numberslen).toString()
    }

    fun getAliases() {
        viewModelScope.launch {
            val alias = ArrayList<String>()
            for (a in aliasDao.getAllUsers()) {
                Log.d("tag", a.alias)
                alias.add(a.alias)
            }
            aliases = alias
        }
    }

    fun putPassList(title: String, description: String, id: String, alias: String) {
        viewModelScope.launch {
            passListDao.insertNewPass(
                PassListEntity(
                    title = title,
                    desc = description,
                    alias = alias,
                    passId = id,
                    lastModify = 0L
                )
            )
        }
    }

    fun putPasses(passId: String, userId: String, password: String, alias: String) {
        viewModelScope.launch {
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
}