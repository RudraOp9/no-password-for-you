package com.leo.nopasswordforyou.helper;

import android.os.Build;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Security {

    String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    String PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    String TRANSFORMATION = ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING;

    Cipher cipher;
    KeyStore keyStore;
    String alias = "NOPASSWORDFORYOUKEY";
    public Security() {

        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException |
                CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String encryptData(String pass) {
        String encPass;
        try {

            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            encPass = new String(Base64.encode(cipher.doFinal(pass.getBytes()), Base64.DEFAULT));
            Log.d("tag","in encryptData try block ");

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            Log.d("tag","in encryptData catch block");
            throw new RuntimeException(e);
        }
        return encPass;
    }

    public String decryptData(String pass) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE,getKey(),new IvParameterSpec(cipher.getIV()));
        return new String(cipher.doFinal(Base64.decode(pass,Base64.DEFAULT)), StandardCharsets.UTF_8);
    }

    private Key getKey() {

        try {
            if(keyStore.containsAlias(alias)){
                Log.d("tag","key store contains the key");
               return (Key) keyStore.getEntry(alias,null);
            }else return newKey();
        } catch (KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException e) {
            Log.d("tag","in getKey catch block");
            throw new RuntimeException(e);
        }

    }

    private Key newKey() {
        String keyText = "1234567890123456";
        Log.d("tag","creating new key");
        SecureRandom random = new SecureRandom();
        byte[] EncryptionKey = new byte[32];
        random.nextBytes(EncryptionKey);
        return new SecretKeySpec(keyText.getBytes(StandardCharsets.UTF_8),"AES");
    }


}
