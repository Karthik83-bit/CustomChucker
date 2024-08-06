package com.isu.customchucker

import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.text.ParseException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author karthik
 * Encrypt decrypt
 *
 * @constructor Create empty Encrypt decrypt
 * class to handle encryption decryption of request
 * and responses
 */
object EncryptDecrypt {

    private  fun getEncryptionKey():String="XHgxl8qs5D6sejZncSsYg7OqPIeV0uQe5I9Zh+uHLcc="

    private fun concatenateByteArrays(a: ByteArray, b: ByteArray): ByteArray {
        return ByteBuffer.allocate(a.size + b.size).put(a).put(b).array()
    }

    private fun base64Decoding(input: String?): ByteArray {
        return Base64.decode(input, Base64.DEFAULT)
    }

    /**
     * Generate random nonce
     * function to generate a random no-once bytearray for GCM generation
     * @return
     */

    private fun generateRandomNonce(): ByteArray {
        val secureRandom = SecureRandom()
        val nonce = ByteArray(12)
        secureRandom.nextBytes(nonce)
        return nonce
    }

    /**
     * Base64encoding
     *  function to encode to Base64
     * @param input
     * @return
     */
    private fun base64Encoding(input: ByteArray): String {
        return Base64.encodeToString(input,Base64.DEFAULT)
    }

    /**
     * Android print exception
     * function to log exceptions
     * @param e
     */
    fun androidPrintException(e: Exception) {

            Log.d(
                "ENCDEC_Exception",
                e.message.toString() + "\n" + e.localizedMessage!!.toString() + "\n" + e.toString()
            )


    }

    /**
     * Android print
     * function to make specificlogs
     * @param encrypted text
     */
    fun androidPrint(encryptedtext: String) {

            Log.d("ENCDEC", "encrypRequest: $encryptedtext")


    }


    /**
     * Aes gcm encrypt to base64
     *
     * @param key
     * @param data
     * @return
     *  function to encrypt the request data
     *  using GCM encryption iv ,auth encryption
     *  and convert to encrypted data class
     *
     */
    fun aesGcmEncryptToEncryptedDataClass(key: String= getEncryptionKey(), data: String): EncryptedData? {
        val random = SecureRandom()
        val salt = ByteArray(8)
        random.nextBytes(salt)
        val iv = ByteArray(12)
        random.nextBytes(iv)
        androidPrint("Ecrypted(before):"+data.toString())


        val nonce = generateRandomNonce()
        val secretKeySpec = SecretKeySpec(base64Decoding(key), "AES")
        val gcmParameterSpec = GCMParameterSpec(16 * 8, nonce)
        var encryptedDto: EncryptedData? = null

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec)
        val dataString = data
        val ciphertextWithTag = cipher.doFinal(dataString.toByteArray(StandardCharsets.UTF_8))
        val ciphertext = ciphertextWithTag.copyOfRange(0, ciphertextWithTag.size - 16)
        val gcmTag = ciphertextWithTag.copyOfRange(ciphertextWithTag.size - 16, ciphertextWithTag.size)
        val nonceBase64 = base64Encoding(nonce)
        val ciphertextBase64 = base64Encoding(ciphertext)
        val gcmTagBase64 = base64Encoding(gcmTag)

        encryptedDto = EncryptedData()
        encryptedDto.iv = nonceBase64.trim()

        encryptedDto.encryptedMessage = ciphertextBase64.trim()

        encryptedDto.authTag = gcmTagBase64.trim()
        androidPrint("Ecrypted(after):"+encryptedDto.toString())


        return encryptedDto
    }

    /**
     * Aes gcm decrypt from base64
     *
     * @param key
     * @param encrypted
     * @return
     * function to decrypt the response
     * using GCM iv,auth tag
     * to map the required response
     */
    fun aesGcmDecryptFromBase64(key: String= getEncryptionKey(), encrypted: EncryptedData): String ?{
        val nonce = base64Decoding(encrypted.iv)
        val ciphertext = base64Decoding(encrypted.encryptedMessage)
        val gcmTag = base64Decoding(encrypted.authTag)
        val encryptedData = concatenateByteArrays(ciphertext, gcmTag)
        var decryptedData: String? = null
        try {
            androidPrint("Decrypted(before):$encrypted")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val secretKeySpec = SecretKeySpec(base64Decoding(key), "AES")
            val gcmParameterSpec = GCMParameterSpec(16 * 8, nonce)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec)
            val decryptedString = String(cipher.doFinal(encryptedData))

            decryptedData = decryptedString
            androidPrint("Decrypted(after):$decryptedData")

        } catch (pe: ParseException) {
            androidPrintException(pe)
            System.err.println(pe.message)
        } catch (e: Exception) {
            androidPrintException(e)
            System.err.println(e.message)
        }
        return decryptedData
    }

    fun aesGcmDecryptFromBase64FromJsonObject(key: String= getEncryptionKey(), encrypted: JSONObject): String ?{

        val iv = encrypted.getString("iv")
        val encryptedMessage = encrypted.getString("encryptedMessage")
        val authTag = encrypted.getString("authTag")
        val nonce = base64Decoding(iv)
        val ciphertext = base64Decoding(encryptedMessage)
        val gcmTag = base64Decoding(authTag)
        val encryptedData = concatenateByteArrays(ciphertext, gcmTag)
        var decryptedData: String? = null
        try {
            androidPrint("Decrypted(before):$encrypted")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val secretKeySpec = SecretKeySpec(base64Decoding(key), "AES")
            val gcmParameterSpec = GCMParameterSpec(16 * 8, nonce)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec)
            val decryptedString = String(cipher.doFinal(encryptedData))

            decryptedData = decryptedString
            androidPrint("Decrypted(after):$decryptedData")

        } catch (pe: ParseException) {
            androidPrintException(pe)
            System.err.println(pe.message)
        } catch (e: Exception) {
            androidPrintException(e)
            System.err.println(e.message)
        }
        return decryptedData
    }


}