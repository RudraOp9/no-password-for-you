package com.leo.nopasswordforyou.helper;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



public class Security {

    final String ALGORITHM = "RSA";
    final String BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB;
    final String PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;
    final String KEYSTORE = "AndroidKeyStore";
    final String TRANSFORMATION = String.format("%S/%S/%S", ALGORITHM, BLOCK_MODE, PADDING);
    Cipher cipher;
    KeyStore keyStore;
    Context context;
    String alias;

    public Security(Context context, String alias) throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        this.context = context;
        cipher = Cipher.getInstance(TRANSFORMATION);
        keyStore = KeyStore.getInstance(KEYSTORE);
        keyStore.load(null);
        this.alias = alias;

    }

    public String encryptData(String pass) throws InvalidAlgorithmParameterException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnrecoverableEntryException, InvalidKeySpecException {
        String encPass;

        cipher.init(Cipher.ENCRYPT_MODE, getKey(Cipher.ENCRYPT_MODE));
        encPass = new String(Base64.encode(cipher.doFinal(pass.getBytes()), Base64.DEFAULT));
        Log.d("tag", "in encryptData try block ");


        return encPass;
    }

    public String decryptData(String pass) throws
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException,
            NoSuchAlgorithmException,
            KeyStoreException,
            NoSuchProviderException,
            UnrecoverableEntryException, InvalidKeySpecException {

        cipher.init(Cipher.DECRYPT_MODE, getKey(Cipher.DECRYPT_MODE));
        return new String(cipher.doFinal(Base64.decode(pass, Base64.DEFAULT)), StandardCharsets.UTF_8);
    }

    private Key getKey(int mode) throws
            RuntimeException,
            KeyStoreException,
            InvalidAlgorithmParameterException,
            NoSuchAlgorithmException,
            NoSuchProviderException,
            InvalidKeyException,
            UnrecoverableEntryException {

        if (mode == Cipher.ENCRYPT_MODE) {
            if (keyStore.containsAlias(alias)) {
                Log.d("tag", " encrypting contains the key");
                return keyStore.getCertificate(alias).getPublicKey();

            } else return newKey();
        } else {
            if (keyStore.containsAlias(alias)) {
                Log.d("tag", "key store decrypting contains the key");
                return keyStore.getKey(alias, null);
            } else {
                throw new InvalidKeyException("No Key Found : Add Keys First");
            }
        }


    }

    private Key newKey() throws
            NoSuchAlgorithmException,
            NoSuchProviderException,
            InvalidAlgorithmParameterException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, KEYSTORE);
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(PADDING)
                .build();

        keyPairGenerator.initialize(keyGenParameterSpec);

        Log.d("tag","done in new Key returning keypublic");
        return keyPairGenerator.generateKeyPair().getPublic();

    }

}
