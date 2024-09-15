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

package com.leo.nopasswordforyou.secuirity

data class KeyFile(
    var privateKeyEncoded: ByteArray, var privateKeyFormat: String,
    var publicKeyEncoded: ByteArray, var publicKeyFormat: String,
    var alias: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyFile

        if (!privateKeyEncoded.contentEquals(other.privateKeyEncoded)) return false
        if (privateKeyFormat != other.privateKeyFormat) return false
        if (!publicKeyEncoded.contentEquals(other.publicKeyEncoded)) return false
        if (publicKeyFormat != other.publicKeyFormat) return false
        if (alias != other.alias) return false

        return true
    }

    override fun hashCode(): Int {
        var result = privateKeyEncoded.contentHashCode()
        result = 31 * result + privateKeyFormat.hashCode()
        result = 31 * result + publicKeyEncoded.contentHashCode()
        result = 31 * result + publicKeyFormat.hashCode()
        result = 31 * result + alias.hashCode()
        return result
    }

}
