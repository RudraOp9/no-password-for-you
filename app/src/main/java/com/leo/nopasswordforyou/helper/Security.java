package com.leo.nopasswordforyou.helper;

import android.credentials.GetCredentialException;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Objects;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kotlin.jvm.Throws;


public class Security {

    final String ALGORITHM = "RSA";
    final String BLOCK_MODE = "CBC";
    final String PADDING = "PKCS1Padding";
    final String KEYSTORE = "AndroidKeyStore";
    final String TRANSFORMATION = String.format("%S/%S/%S",ALGORITHM,BLOCK_MODE,PADDING) ;
    Cipher cipher;
    KeyStore keyStore;
    final String alias = "NOPASSWORDFORYOUKEY"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    public Security() throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {


            cipher = Cipher.getInstance(TRANSFORMATION);
            keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null);

    }
    public String encryptData(String pass) throws InvalidAlgorithmParameterException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnrecoverableEntryException {
        String encPass;

            cipher.init(Cipher.ENCRYPT_MODE, getKey(Cipher.ENCRYPT_MODE));
            encPass = new String(Base64.encode(cipher.doFinal(pass.getBytes()), Base64.DEFAULT));
            Log.d("tag","in encryptData try block ");


        return encPass;
    }

    public String decryptData(String pass) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException {
        cipher.init(Cipher.DECRYPT_MODE,getKey(Cipher.DECRYPT_MODE)
                ,new IvParameterSpec(cipher.getIV()));
        return new String(cipher.doFinal(Base64.decode(pass,Base64.DEFAULT)), StandardCharsets.UTF_8);
    }

    private Key getKey(int mode) throws RuntimeException, KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, UnrecoverableEntryException {

        if (mode == Cipher.ENCRYPT_MODE){
            if(keyStore.containsAlias(alias)){
                Log.d("tag","key store contains the key");
                return keyStore.getCertificate(alias).getPublicKey();
            }else return newKey();
        }else{
            if(keyStore.containsAlias(alias)){
                Log.d("tag","key store contains the key");
                return keyStore.getKey(alias,null);
            }else {
                throw new InvalidKeyException("No Key Found : Add Keys First");
            }
        }



    }

    private Key newKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM,KEYSTORE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyGenParameterSpec keyGenParameterSpec  = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setUserAuthenticationRequired(true)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1).build();

            keyPairGenerator.initialize(keyGenParameterSpec);

        }else {
            keyPairGenerator.initialize(2048);
        }

        KeyPair keyPair =  keyPairGenerator.generateKeyPair();
      //  keyStore.
        Key keyPublic = keyPair.getPublic();
        Key keyPrivate =  keyPair.getPrivate();
        return keyPublic ;
    }


}
