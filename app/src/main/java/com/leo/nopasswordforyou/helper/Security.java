package com.leo.nopasswordforyou.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.spec.IvParameterSpec;


public class Security {

    final String ALGORITHM = "RSA";
    final String BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB;
    final String PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;
    final String KEYSTORE = "AndroidKeyStore";
    final String TRANSFORMATION = String.format("%S/%S/%S", ALGORITHM, BLOCK_MODE, PADDING);
    Cipher cipher;
    KeyStore keyStore;
    Context context;
    final String alias = "NOPASSWORDF!!!!" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public Security(Context context) throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        this.context = context;
        cipher = Cipher.getInstance(TRANSFORMATION);
        keyStore = KeyStore.getInstance(KEYSTORE);
        keyStore.load(null);

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
            UnrecoverableEntryException, InvalidKeySpecException {

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
