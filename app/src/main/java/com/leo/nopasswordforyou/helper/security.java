package com.leo.nopasswordforyou.helper;

import android.os.Build;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.RequiresApi;


import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;

@RequiresApi(api = Build.VERSION_CODES.M)
public class security {

    String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    String PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    String TRANSFORMATION = ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING;

    Cipher cipher;
    KeyStore keyStore;
    security() {

        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }
    public void encryptData(String pass){
        try {
            cipher.init(Cipher.ENCRYPT_MODE,getKey());
           // new IvParameterSpec(cipher.getIV());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String encPass = new String(Base64.encode(cipher.doFinal(pass.getBytes()), Base64.DEFAULT));
            }
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey getKey() {
        String alias = "NOPASSWORDFORYOUKEY";
        try {
            if(keyStore.containsAlias(alias)){
               return (SecretKey) keyStore.getEntry(alias,null);
            }else return newKey();
        } catch (KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private SecretKey newKey() {
        String keyText = "something";
        return new SecretKeySpec(keyText.getBytes(StandardCharsets.UTF_8),"AES");
    }

    public void decryptData(String enPass){

    }
}
