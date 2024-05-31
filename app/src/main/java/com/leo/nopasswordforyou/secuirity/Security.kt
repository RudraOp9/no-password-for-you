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
package com.leo.nopasswordforyou.secuirity

import android.content.Context
import android.os.Environment
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.Security
import java.security.UnrecoverableEntryException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Date
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


class Security(var context: Context) {
    val ALGORITHM: String = "RSA"
    val BLOCK_MODE: String = KeyProperties.BLOCK_MODE_ECB
    val PADDING: String = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
    val KEYSTORE: String = "AndroidKeyStore"
    val TRANSFORMATION: String = String.format("%S/%S/%S", ALGORITHM, BLOCK_MODE, PADDING)
    var cipher: Cipher? = null
    var keyStore: KeyStore? = null


    init {
        try {
            cipher = Cipher.getInstance(TRANSFORMATION)
        } catch (e: NoSuchAlgorithmException) {
            Toast.makeText(context, "Error : Contact Support", Toast.LENGTH_SHORT).show()
        } catch (e: NoSuchPaddingException) {
            Toast.makeText(context, "Error : Contact Support", Toast.LENGTH_SHORT).show()
        }
        try {
            keyStore = KeyStore.getInstance(KEYSTORE)
        } catch (e: KeyStoreException) {
            Toast.makeText(context, "Error : Device problem , contact support", Toast.LENGTH_SHORT)
                .show()
        }
        try {
            keyStore!!.load(null)
        } catch (e: IOException) {
            Toast.makeText(context, "something went unusual", Toast.LENGTH_SHORT).show()
        } catch (e: NoSuchAlgorithmException) {
            Toast.makeText(context, "Error : Contact Support team", Toast.LENGTH_SHORT).show()
        } catch (e: CertificateException) {
            Toast.makeText(context, "Error : Contact Support team", Toast.LENGTH_SHORT).show()
        }

    }

    fun encryptData(pass: String, alias: String, error: (String) -> Unit): String? {
        try {
            val key = getKey(Cipher.ENCRYPT_MODE, alias, error)
            if (key != null) {
                cipher!!.init(Cipher.ENCRYPT_MODE, key)
                return String(Base64.encode(cipher!!.doFinal(pass.toByteArray()), Base64.DEFAULT))
            } else return null
        } catch (e: InvalidKeyException) {
            error.invoke("Incorrect Key Chosen ! code 39")
        } catch (e: KeyStoreException) {
            error.invoke("error code 100")
        } catch (e: UnrecoverableEntryException) {
            error.invoke("Import key again ! code 50")
        } catch (e: NoSuchAlgorithmException) {
            error.invoke("error code 101")
        } catch (e: BadPaddingException) {
            error.invoke("Wrong Key or Incorrect Data , code 129")
        } catch (e: IllegalBlockSizeException) {
            error.invoke("Wrong Key or Incorrect Data , code 130")
        }
        Log.d("tag", "in encryptData try block ")
        return null
    }

    fun decryptData(pass: String?, alias: String, error: (String) -> Unit): String? {
        try {
            val key = getKey(Cipher.DECRYPT_MODE, alias, error)
            if (key != null) {
                cipher!!.init(Cipher.DECRYPT_MODE, key)
                return String(
                    cipher!!.doFinal(Base64.decode(pass, Base64.DEFAULT)),
                    StandardCharsets.UTF_8
                )
            } else return null
        } catch (e: InvalidKeyException) {
            error.invoke("Incorrect Key Chosen ! code 39")
        } catch (e: KeyStoreException) {
            error.invoke("error code 100")
        } catch (e: UnrecoverableEntryException) {
            error.invoke("Import key again ! code 50")
        } catch (e: NoSuchAlgorithmException) {
            error.invoke("error code 101")
        } catch (e: BadPaddingException) {
            error.invoke("Wrong Key or Incorrect Data , code 129")
        } catch (e: IllegalBlockSizeException) {
            error.invoke("Wrong Key or Incorrect Data , code 130")
        }

        return null
    }

    @Throws(
        RuntimeException::class,
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        UnrecoverableEntryException::class
    )
    private fun getKey(mode: Int, alias: String, error: (String) -> Unit): Key? {
        if (mode == Cipher.ENCRYPT_MODE) {
            if (keyStore!!.containsAlias(alias)) {
                Log.d("tag", " encrypting contains the key")
                return keyStore!!.getCertificate(alias).publicKey
            } else {
                error.invoke("Import key again")
                return null
            }
        } else {
            if (keyStore!!.containsAlias(alias)) {
                Log.d("tag", "key store decrypting contains the key")
                return keyStore!!.getKey(alias, null)
            } else {
                throw InvalidKeyException("No Key Found : Add Keys First")
            }
        }
    }

    fun newKey(alias: String): String {
        val keyPairGenerator: KeyPairGenerator
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            return "Error" + e.localizedMessage
        }

        /* KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(PADDING)
                .build();*/
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.genKeyPair()
        /*
        keyStore.setKeyEntry(alias+"public",keyPair.getPublic(),null,null);
        Enumeration<String> a22 = keyStore.aliases();
        while (a22.hasMoreElements()) Log.d("tagy", a22.nextElement());*/
        val cert: X509Certificate
        try {
            cert = generateSelfSignedCertificate(keyPair)
            keyStore!!.setKeyEntry(
                alias,
                keyPair.private, null, arrayOf<Certificate>(cert)
            )
        } catch (e: Exception) {
            return "Error" + e.localizedMessage
        }
        try {
            val e = keyStore!!.aliases()
            while (e.hasMoreElements()) {
                Log.d("TAG", "newKey:" + e.nextElement())
            }
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        }


        val a = KeyFile(
            keyPair.private.encoded,
            keyPair.private.format,
            keyPair.public.encoded,
            keyPair.public.format,
            alias
        )

        val gson = Gson()
        val json = gson.toJson(a)

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (dir != null) {
            val jsonData = File(dir.path, "$alias.ppk")
            try {
                jsonData.createNewFile()
            } catch (e: IOException) {
                return "can't create file"
            }
            if (jsonData.canWrite()) {
                try {
                    val writer = FileOutputStream(jsonData)
                    writer.write(json.toByteArray())
                    writer.close()
                } catch (e: IOException) {
                    return "Failed : " + e.localizedMessage
                }
            } else return "Need Permission to save File"
        } else return "something went wrong"
        return "done"

        // return keyPairGenerator.generateKeyPair().getPublic();
    }

    fun importKey(file: String, result: (String) -> Unit) {
        val gson = Gson()

        val data = gson.fromJson(file.reader(), KeyFile::class.java)

        //private key
        val privateBytes = PKCS8EncodedKeySpec(data.privateKeyEncoded)
        val privateKeyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val pvt: PrivateKey = privateKeyFactory.generatePrivate(privateBytes)

        //public key
        val ks = X509EncodedKeySpec(data.publicKeyEncoded)
        val kf = KeyFactory.getInstance("RSA")
        val pub = kf.generatePublic(ks)

        val keyPair = KeyPair(pub, pvt)

        val cert: X509Certificate
        try {
            cert = generateSelfSignedCertificate(keyPair)
            keyStore?.setKeyEntry(
                data.alias,
                keyPair.private, null, arrayOf<Certificate>(cert)
            )
            result.invoke(data.alias)
        } catch (e: Exception) {
            result.invoke("error")
        }
    }

    companion object {
        @Throws(Exception::class)
        fun generateSelfSignedCertificate(keyPair: KeyPair): X509Certificate {
            Security.setProperty("crypto.policy", "unlimited")
            val issuer = X500Name("CN= No PassWord For You")
            val subject = X500Name("CN= PassWord Encryption")
            val serialNumber = BigInteger.valueOf(System.currentTimeMillis())

            val publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)

            val certBuilder: X509v3CertificateBuilder = JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                Date(System.currentTimeMillis()),
                Date(System.currentTimeMillis() * 2),
                subject,
                publicKeyInfo
            )

            certBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                Extension.subjectKeyIdentifier
            )
            certBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                Extension.authorityKeyIdentifier
            )
            certBuilder.addExtension(Extension.basicConstraints, true, BasicConstraints(true))

            val signer = JcaContentSignerBuilder("SHA256withRSA").build(keyPair.private)
            val certHolder = certBuilder.build(signer)

            return JcaX509CertificateConverter().getCertificate(certHolder)
        }
    }
}
