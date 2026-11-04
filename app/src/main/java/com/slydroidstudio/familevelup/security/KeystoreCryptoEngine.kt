package com.slydroidstudio.familevelup.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object KeystoreCryptoEngine {

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "FamiLevelUpMasterKey_v1"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_SIZE = 12

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun encryptFile(inputFile: File, outputFile: File) {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv

        FileOutputStream(outputFile).use { outStream ->
            outStream.write(iv)
            FileInputStream(inputFile).use { inStream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (inStream.read(buffer).also { bytesRead = it } != -1) {
                    val cipherBytes = cipher.update(buffer, 0, bytesRead)
                    if (cipherBytes != null) {
                        outStream.write(cipherBytes)
                    }
                }
                val finalBytes = cipher.doFinal()
                if (finalBytes != null) {
                    outStream.write(finalBytes)
                }
            }
        }
    }

    fun decryptFile(inputFile: File, outputFile: File) {
        val secretKey = getOrCreateSecretKey()

        FileInputStream(inputFile).use { inStream ->
            val iv = ByteArray(IV_SIZE)
            val readIv = inStream.read(iv)
            if (readIv != IV_SIZE) throw IllegalArgumentException("Fichier chiffré invalide ou corrompu")

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            FileOutputStream(outputFile).use { outStream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (inStream.read(buffer).also { bytesRead = it } != -1) {
                    val plainBytes = cipher.update(buffer, 0, bytesRead)
                    if (plainBytes != null) {
                        outStream.write(plainBytes)
                    }
                }
                val finalBytes = cipher.doFinal()
                if (finalBytes != null) {
                    outStream.write(finalBytes)
                }
            }
        }
    }
}
