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

package com.leo.nopasswordforyou.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.alias.AliasEntity
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListDao
import com.leo.nopasswordforyou.database.passlist.PassListEntity


@Database(entities = [PassListEntity::class, PassesEntity::class, AliasEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun passListDao(): PassListDao
    abstract fun passesDao(): PassesDao
    abstract fun aliasDao(): AliasDao
}