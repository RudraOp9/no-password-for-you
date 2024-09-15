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

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passes")
data class PassesEntity(
    @PrimaryKey(autoGenerate = false)
    val passId: String,
    val userId: String,
    val password: String,
    val alias: String,
    var masterPassReq: Boolean = false
) {
    constructor() : this("", "", "", "")
}
