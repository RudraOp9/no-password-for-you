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

package com.leo.nopasswordforyou.util

import androidx.core.util.toRange

class NewPass {
    //   private var c = ('a'..'b').toList()
    private var capitalLetter = ('A'..'Z').toList()
    private var smallLetter = ('a'..'z').toList()
    private var numbers = ('0'..'9').toList()
    private var specials = arrayOf('#', '$', '&', '*', '@', '~', '?', '=', '/', ':')
    fun generateNewPass(
        alphaCapLength: Int,
        specialSymbol: Int,
        numbersLen: Int,
        alphaSmallLength: Int,
    ): String {


        val password = mutableListOf<Char>()
        for (a in 0 until alphaSmallLength) {
            password.add(smallLetter[(Math.random() * 25).toInt()])
        }
        for (d in 0 until specialSymbol) {
            password.add(specials[(Math.random() * 9).toInt()])
        }
        for (c in 0 until numbersLen) {
            password.add(numbers[(Math.random() * 9).toInt()])
        }
        for (b in 0 until alphaCapLength) {
            password.add(capitalLetter[(Math.random() * 25).toInt()])
        }
        password.shuffle()
        return password.joinToString("", "", "", -1, "", null)
    }
}
