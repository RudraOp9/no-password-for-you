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

package com.leo.nopasswordforyou.database.passes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PassesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewPass(passesEntity: PassesEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPass(passesEntities: List<PassesEntity>)

    @Query("SELECT * FROM passes WHERE passId = :keyId")
    suspend fun getPass(keyId: String): PassesEntity?

    @Query("SELECT * FROM passes")
    suspend fun getAllPass(): List<PassesEntity>

    @Update
    suspend fun updatePass(passesEntity: PassesEntity)

    @Delete
    suspend fun deletePass(passesEntity: PassesEntity)

    @Delete
    suspend fun deleteAllPass(passesEntities: List<PassesEntity>)

    suspend fun deletePass(keyId: String) {
        getPass(keyId)?.let { deletePass(it) }
    }

    suspend fun deleteAll() {
        deleteAllPass(getAllPass())
    }





}