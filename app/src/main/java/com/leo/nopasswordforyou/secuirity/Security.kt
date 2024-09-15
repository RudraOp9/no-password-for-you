/*
 *  No password for you
 *  Copyright (c) 2024 . All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,either version 3 of the License,or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not,see <http://www.gnu.org/licenses/>.
 */
package com.leo.nopasswordforyou.secuirity

import android.content.Context
import android.os.Environment
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
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

    private val ALGORITHM: String = "RSA"
    private val BLOCK_MODE: String = KeyProperties.BLOCK_MODE_ECB
    private val PADDING: String = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
    private val KEYSTORE: String = "AndroidKeyStore"
    private val TRANSFORMATION: String = String.format("%S/%S/%S", ALGORITHM, BLOCK_MODE, PADDING)
    private lateinit var cipher: Cipher
    private var keyStore: KeyStore? = null


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
                cipher.init(Cipher.ENCRYPT_MODE, key)
                return String(Base64.encode(cipher.doFinal(pass.toByteArray()), Base64.DEFAULT))
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
                cipher.init(Cipher.DECRYPT_MODE, key)
                return String(
                    cipher.doFinal(Base64.decode(pass, Base64.DEFAULT)),
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
        } catch (e: Exception) {
            error.invoke("Something went wrong")
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

    fun removeKey(alias: String, error: (String) -> Unit, success: () -> Unit) {
        try {
            if (keyStore?.containsAlias(alias) == true) {
                keyStore?.deleteEntry(alias)
                success.invoke()
            } else {
                success()
                error("Key not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error.invoke(e.localizedMessage ?: "Error while removing from device")
        }
    }

    fun newKey(alias: String): String {
        val keyPairGenerator: KeyPairGenerator
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            return "Error" + e.localizedMessage
        }

        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.genKeyPair()

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

        val keyFile = KeyFile(
            keyPair.private.encoded,
            keyPair.private.format,
            keyPair.public.encoded,
            keyPair.public.format,
            alias
        )

        val gson = Gson()
        val json = gson.toJson(keyFile)

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

    fun importKey(file: String, result: (String, Int) -> Unit) {
        val gson = Gson()

        try {
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

            val cert: X509Certificate = generateSelfSignedCertificate(keyPair)

            keyStore?.setKeyEntry(
                data.alias,
                keyPair.private, null, arrayOf<Certificate>(cert)
            )
            result.invoke(data.alias, 0)

        } catch (e: JsonIOException) {
            result.invoke("Error reading file", 1)
        } catch (e: JsonSyntaxException) {
            result.invoke("File corrupted", 1)
        } catch (e: Exception) {
            e.printStackTrace()
            result.invoke("Error creating key", 1)
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
