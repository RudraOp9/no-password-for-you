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

package com.leo.nopasswordforyou.database.passlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface PassListDao {

    @Insert
    suspend fun insertNewPass(passListEntity: PassListEntity)

    @Insert
    suspend fun insertAllNewPass(passListEntities: List<PassListEntity>)

    @Query("SELECT * FROM passList")
    suspend fun getPassList(): List<PassListEntity>

    @Query("SELECT * FROM passList WHERE passId = :keyId")
    suspend fun getPassList(keyId: String): PassListEntity?

    @Update
    suspend fun updatePassList(passListEntity: PassListEntity)

    @Delete
    suspend fun deletePassList(passListEntity: PassListEntity)

    @Delete
    suspend fun deleteAllPassList(passListEntities: List<PassListEntity>)




    suspend fun deletePassList(keyId: String) {
        getPassList(keyId)?.let { deletePassList(it) }
    }

    suspend fun deleteAllPassList() {
        deleteAllPassList(getPassList())
    }







}