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

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "passList")
data class PassListEntity(
    @PrimaryKey(autoGenerate = false)
    val passId: String,
    val title: String,
    val desc: String,
    val alias: String,
    val lastModify: Long,
    val onCloud: Boolean
) {
    constructor() : this("", "", "", "", 0L, false)
}


