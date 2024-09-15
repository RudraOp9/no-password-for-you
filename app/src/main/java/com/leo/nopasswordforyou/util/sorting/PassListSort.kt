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

package com.leo.nopasswordforyou.util.sorting

import com.leo.nopasswordforyou.database.passlist.PassListEntity

fun sortPasswords(
    passes: List<PassListEntity>,
    sortOrder: SortOrder,
    sortBy: Sorting,
    result: (List<PassListEntity>) -> Unit
) {
    if (sortOrder == SortOrder.Ascending) {
        when (sortBy) {
            Sorting.Modified -> {
                result(passes.sortedBy {
                    it.lastModify
                })
            }

            Sorting.Alias -> {
                result(passes.sortedBy {
                    it.alias
                })
            }

            else -> {
                result(passes.sortedBy {
                    it.title
                })
            }
        }
    } else {
        when (sortBy) {
            Sorting.Modified -> {
                result(passes.sortedByDescending {
                    it.lastModify
                })
            }

            Sorting.Alias -> {
                result(passes.sortedByDescending {
                    it.alias
                })
            }

            else -> {
                result(passes.sortedByDescending {
                    it.title
                })
            }
        }
    }

}