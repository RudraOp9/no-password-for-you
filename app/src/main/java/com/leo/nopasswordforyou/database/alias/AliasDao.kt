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

package com.leo.nopasswordforyou.database.alias

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AliasDao {
    @Insert
    suspend fun insertNewAlias(aliasEntity: AliasEntity)

    @Query("SELECT *  FROM alias")
    suspend fun getAllAlias(): List<AliasEntity>


    @Delete
    suspend fun deleteAlias(aliasEntity: AliasEntity)
}