/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 02/06/24, 6:08 pm
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityVm @Inject constructor(val aliasDao: AliasDao) : ViewModel() {
    fun setAlias(alias: String) {
        viewModelScope.launch {
            aliasDao.insertNewAlias(AliasEntity(alias = alias))
        }
    }
}