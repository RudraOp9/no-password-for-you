package com.leo.nopasswordforyou.helper

class NewPass {
    private var alphaCapLength: Byte = 0
    private var specialSymbol: Byte = 0
    private var numberslen: Byte = 0
    private var alphaSmallLength: Byte = 6
    private var passLength: Byte = 16
    private var capitalLetter = arrayOf(
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
        numbersLen: Byte,
        alphaSmallLength: Byte,
        passLength: Byte
    ): String {
        this.alphaCapLength = alphaCapLength
        this.specialSymbol = specialSymbol
        this.numberslen = numbersLen
        this.alphaSmallLength = alphaSmallLength
        this.passLength = passLength
        val password = ArrayList<String?>(passLength.toInt())
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
