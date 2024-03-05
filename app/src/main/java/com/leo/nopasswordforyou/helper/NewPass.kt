package com.leo.nopasswordforyou.helper;

import java.util.ArrayList;
import java.util.Collections;

public class NewPass {
    byte alphaCapLength;
    byte specialSymbol;
    byte numberslen;
    byte alphaSmallLength = 6;
    byte passLength = 16;
    String[] capitalLetter = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    String[] smallLetter = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y"};
    String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String[] specials = {"#", "$", "&", "*", "@", "~", "?", "=", "/", ":"};


    public NewPass() {

    }

    public String generateNewPass(byte alphaCapLength, byte specialSymbol, byte numberslen, byte alphaSmallLength, byte passLength) {
        this.alphaCapLength = alphaCapLength;
        this.specialSymbol = specialSymbol;
        this.numberslen = numberslen;
        this.alphaSmallLength = alphaSmallLength;
        this.passLength = passLength;


        ArrayList<String> password = new ArrayList<>(passLength);


        for (byte a = 0; a < alphaSmallLength; a++) {
            password.add(smallLetter[(int) (Math.random() * 25)]);
        }
        for (byte d = 0; d < specialSymbol; d++) {
            password.add(specials[(int) (Math.random() * 9)]);
        }
        for (byte c = 0; c < numberslen; c++) {
            password.add(numbers[(int) (Math.random() * 9)]);
        }

        for (byte b = 0; b < alphaCapLength; b++) {
            password.add(capitalLetter[(int) (Math.random() * 25)]);
        }


        Collections.shuffle(password);

        StringBuilder pass = new StringBuilder();
        for (String a : password) pass.append(a);
        return pass.toString();

    }

}
