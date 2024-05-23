/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 22/05/24, 10:55 am
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

package com.leo.nopasswordforyou.secuirity;

import static org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier;
import static org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier;

import android.content.Context;
import android.os.Environment;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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


    public Security(Context context, String alias) {
        this.context = context;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Toast.makeText(context, "Error : Contact Support", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            keyStore = KeyStore.getInstance(KEYSTORE);
        } catch (KeyStoreException e) {
            Toast.makeText(context, "Error : Device problem , contact support", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            keyStore.load(null);
        } catch (IOException e) {
            Toast.makeText(context, "something went unusual", Toast.LENGTH_SHORT).show();
            return;
        } catch (NoSuchAlgorithmException | CertificateException e) {
            Toast.makeText(context, "Error : Contact Support team", Toast.LENGTH_SHORT).show();
            return;
        }
        this.alias = alias;

    }

    public static X509Certificate generateSelfSignedCertificate(KeyPair keyPair) throws Exception {
        java.security.Security.setProperty("crypto.policy", "unlimited");
        X500Name issuer = new X500Name("CN= No PassWord For You");
        X500Name subject = new X500Name("CN= PassWord Encryption");
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() * 2),
                subject,
                publicKeyInfo);

        certBuilder.addExtension(subjectKeyIdentifier, false, subjectKeyIdentifier);
        certBuilder.addExtension(authorityKeyIdentifier, false, authorityKeyIdentifier);
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }

    @Nullable
    public String encryptData(String pass, Function1<String, Unit> error) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getKey(Cipher.ENCRYPT_MODE));
            return new String(Base64.encode(cipher.doFinal(pass.getBytes()), Base64.DEFAULT));
        } catch (InvalidKeyException e) {
            error.invoke("Incorrect Key Chosen ! code 39");
        } catch (KeyStoreException e) {
            error.invoke("error code 100");
        } catch (UnrecoverableEntryException e) {
            error.invoke("Import key again ! code 50");
        } catch (NoSuchAlgorithmException e) {
            error.invoke("error code 101");
        } catch (BadPaddingException e) {
            error.invoke("Wrong Key or Incorrect Data , code 129");
        } catch (IllegalBlockSizeException e) {
            error.invoke("Wrong Key or Incorrect Data , code 130");
        }
        Log.d("tag", "in encryptData try block ");
        return null;
    }

    @Nullable
    public String decryptData(String pass, Function1<String, Unit> error) {

        try {
            cipher.init(Cipher.DECRYPT_MODE, getKey(Cipher.DECRYPT_MODE));
            return new String(cipher.doFinal(Base64.decode(pass, Base64.DEFAULT)), StandardCharsets.UTF_8);

        } catch (InvalidKeyException e) {
            error.invoke("Incorrect Key Chosen ! code 39");
        } catch (KeyStoreException e) {
            error.invoke("error code 100");
        } catch (UnrecoverableEntryException e) {
            error.invoke("Import key again ! code 50");
        } catch (NoSuchAlgorithmException e) {
            error.invoke("error code 101");
        } catch (BadPaddingException e) {
            error.invoke("Wrong Key or Incorrect Data , code 129");
        } catch (IllegalBlockSizeException e) {
            error.invoke("Wrong Key or Incorrect Data , code 130");
        }

        return null;
    }

    private Key getKey(int mode) throws
            RuntimeException,
            KeyStoreException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            UnrecoverableEntryException {

        if (mode == Cipher.ENCRYPT_MODE) {
            if (keyStore.containsAlias(alias)) {
                Log.d("tag", " encrypting contains the key");
                return keyStore.getCertificate(alias).getPublicKey();

            } else return null;
        } else {
            if (keyStore.containsAlias(alias)) {
                Log.d("tag", "key store decrypting contains the key");
                return keyStore.getKey(alias, null);
            } else {
                throw new InvalidKeyException("No Key Found : Add Keys First");
            }
        }


    }

    public String newKey() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            return "Error" + e.getLocalizedMessage();
        }
       /* KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(PADDING)
                .build();*/

        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
     /*
        keyStore.setKeyEntry(alias+"public",keyPair.getPublic(),null,null);
        Enumeration<String> a22 = keyStore.aliases();
        while (a22.hasMoreElements()) Log.d("tagy", a22.nextElement());*/
        X509Certificate cert;
        try {
            cert = generateSelfSignedCertificate(keyPair);
            keyStore.setKeyEntry(alias + "private", keyPair.getPrivate(), null, new Certificate[]{cert});
        } catch (Exception e) {
            return "Error" + e.getLocalizedMessage();
        }


        var a = new KeyFile(keyPair.getPrivate().getEncoded(),
                keyPair.getPrivate().getFormat(),
                keyPair.getPublic().getEncoded(),
                keyPair.getPublic().getFormat());

        Gson gson = new Gson();
        String json = gson.toJson(a);

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (dir != null) {
            File jsonData = new File(dir.getPath(), alias + ".ppk");
            try {
                jsonData.createNewFile();
            } catch (IOException e) {
                return "can't create file";
            }
            if (jsonData.canWrite()) {
                try {
                    FileOutputStream writer = new FileOutputStream(jsonData);
                    writer.write(json.getBytes());
                    writer.close();
                } catch (IOException e) {
                    return "Failed : " + e.getLocalizedMessage();
                }
            } else return "Need Permission to save File";
        } else return "something went wrong";
        return "done";
        // return keyPairGenerator.generateKeyPair().getPublic();

    }
}
