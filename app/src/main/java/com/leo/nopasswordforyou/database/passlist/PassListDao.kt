/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 26/05/24, 2:54 pm
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

package com.leo.nopasswordforyou.database.passlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.leo.nopasswordforyou.database.passes.PassesEntity


@Dao
interface PassListDao {

    @Insert
    suspend fun insertNewPass(passListEntity: PassListEntity)

    @Insert
    suspend fun insertAllNewPass(passListEntities: List<PassListEntity>)

    @Query("SELECT * FROM passList")
    suspend fun getPassList(): List<PassListEntity>

    @Query("SELECT * FROM passList WHERE passId = :keyId")
    suspend fun getPassList(keyId: String): PassListEntity

    @Update()
    suspend fun updatePassList(passListEntity: PassListEntity)

    @Delete()
    suspend fun deletePassList(passListEntity: PassListEntity)

    @Delete
    suspend fun deleteAllPassList(passListEntities: List<PassListEntity>)




    suspend fun deletePassList(keyId: String) {
        deletePassList(getPassList(keyId))
    }

    suspend fun deleteAllPassList() {
        deleteAllPassList(getPassList())
    }







}