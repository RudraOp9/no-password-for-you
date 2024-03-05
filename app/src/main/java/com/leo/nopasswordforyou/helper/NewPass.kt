package com.leo.nopasswordforyou.helper

import java.util.Collections

class NewPass {
    var alphaCapLength: Byte = 0
    var specialSymbol: Byte = 0
    var numberslen: Byte = 0
    var alphaSmallLength: Byte = 6
    var passLength: Byte = 16
    var capitalLetter = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )
    private var smallLetter = arrayOf(
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "t",
        "u",
        "v",
        "w",
        "x",
        "y",
        "z"
    )
    private var numbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private var specials = arrayOf("#", "$", "&", "*", "@", "~", "?", "=", "/", ":")
    fun generateNewPass(
        alphaCapLength: Byte,
        specialSymbol: Byte,
        numberslen: Byte,
        alphaSmallLength: Byte,
        passLength: Byte
    ): String {
        this.alphaCapLength = alphaCapLength
        this.specialSymbol = specialSymbol
        this.numberslen = numberslen
        this.alphaSmallLength = alphaSmallLength
        this.passLength = passLength
        val password = ArrayList<String?>(passLength.toInt())
        for (a in 0 until alphaSmallLength) {
            password.add(smallLetter[(Math.random() * 25).toInt()])
        }
        for (d in 0 until specialSymbol) {
            password.add(specials[(Math.random() * 9).toInt()])
        }
        for (c in 0 until numberslen) {
            password.add(numbers[(Math.random() * 9).toInt()])
        }
        for (b in 0 until alphaCapLength) {
            password.add(capitalLetter[(Math.random() * 25).toInt()])
        }
        password.shuffle()
        val pass = StringBuilder()
        for (a in password) pass.append(a)
        return pass.toString()
    }
}
