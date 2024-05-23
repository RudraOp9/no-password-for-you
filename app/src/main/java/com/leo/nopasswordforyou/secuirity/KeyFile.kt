/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 22/05/24, 11:10 am
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

package com.leo.nopasswordforyou.secuirity

data class KeyFile(
    var privateKeyEncoded: ByteArray, var privateKeyFormat: String,
    var publicKeyEncoded: ByteArray, var publicKeyFormat: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyFile

        if (!privateKeyEncoded.contentEquals(other.privateKeyEncoded)) return false
        if (privateKeyFormat != other.privateKeyFormat) return false
        if (!publicKeyEncoded.contentEquals(other.publicKeyEncoded)) return false
        if (publicKeyFormat != other.publicKeyFormat) return false

        return true
    }

    override fun hashCode(): Int {
        var result = privateKeyEncoded.contentHashCode()
        result = 31 * result + privateKeyFormat.hashCode()
        result = 31 * result + publicKeyEncoded.contentHashCode()
        result = 31 * result + publicKeyFormat.hashCode()
        return result
    }
}
