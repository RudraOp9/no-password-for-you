/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 27/05/24, 11:06 am
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

package com.leo.nopasswordforyou.database.passes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.leo.nopasswordforyou.database.passlist.PassListEntity

@Dao
interface PassesDao {
    @Insert
    suspend fun insertNewPass(passesEntity: PassesEntity)

    @Query("SELECT * FROM passes WHERE passId = :keyId")
    suspend fun getPass(keyId: String): PassesEntity

    @Update()
    suspend fun updatePass(passesEntity: PassesEntity)

    @Delete()
    suspend fun deletePass(passesEntity: PassesEntity)

    suspend fun deletePass(keyId: String) {
        deletePass(getPass(keyId))
    }
}