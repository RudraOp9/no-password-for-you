package com.leo.nopasswordforyou.helper;

import static org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier;
import static org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier;

import android.content.Context;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

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

import java.io.IOException;
import java.math.BigInteger;
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
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;

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

    public static X509Certificate generateSelfSignedCertificate(KeyPair keyPair) throws Exception {
        java.security.Security.setProperty("crypto.policy", "unlimited");
        org.bouncycastle.asn1.x500.X500Name issuer = new X500Name("CN=Your Issuer");
        org.bouncycastle.asn1.x500.X500Name subject = new X500Name("CN=Your Subject");
        java.math.BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        org.bouncycastle.asn1.x509.SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

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

    public String newKey() throws
            Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
       /* KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(PADDING)
                .build();*/
        Enumeration<String> a22 = keyStore.aliases();
        while (a22.hasMoreElements()) Log.d("tag", a22.nextElement());

        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey key = keyPairGenerator.generateKeyPair().getPrivate();

        String a = privateKeyEntryToString(keyPair.getPublic(), keyPair.getPrivate(), keyPair);


        Log.d("tag", "done in new Key returning keypublic   " + a);
        Enumeration<String> a21 = keyStore.aliases();
        while (a21.hasMoreElements()) Log.d("tag", a21.nextElement());


        return a;
        // return keyPairGenerator.generateKeyPair().getPublic();

    }

    private String privateKeyEntryToString(PublicKey publicKey, PrivateKey key, KeyPair keyPair) throws Exception {

        X509Certificate cert = generateSelfSignedCertificate(keyPair);
        keyStore.setKeyEntry(alias, key, null, new Certificate[]{cert});

        if (key.getEncoded().length == 0) {
            return "null";
        }

        // Convert PrivateKey and Certificate to String
        String privateKeyString = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        String certificateString = Base64.encodeToString(cert.getEncoded(), Base64.DEFAULT);

        // Combine both strings with a separator for later use
        return privateKeyString + "-----BEGIN CERTIFICATE-----\n" + certificateString;
    }

}
