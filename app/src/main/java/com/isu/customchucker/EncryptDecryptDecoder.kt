package com.isu.customchucker

import android.util.Log
import com.isu.apitracker.BodyDecoder
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject

class EncryptDecryptDecoder:BodyDecoder {
    override fun decodeRequest(request: Request): String? {
        val bodyJsonStr=request.body?.toStringRepresentation()
        val bodyJsonObj=JSONObject(bodyJsonStr)

        Log.d("DEC", "decodeRequest: $bodyJsonObj")
        return EncryptDecrypt.aesGcmDecryptFromBase64FromJsonObject(encrypted =bodyJsonObj)
    }

    override fun decodeResponse(response: Response): String? {
        val responseBodyStr=response.body?.string()
        val jsonObject=JSONObject(responseBodyStr)
        Log.d("DEC", "decodeRequest: $jsonObject")
       return   try {
            val statusCode = jsonObject.getInt("statusCode")
            val status = jsonObject.getString("status")
            val statusDesc = jsonObject.getString("statusDesc")

            val dataObject = jsonObject.getJSONObject("data")
            val iv = dataObject.getString("iv")
            val encryptedMessage = dataObject.getString("encryptedMessage")
            val authTag = dataObject.getString("authTag")
            println("IV: $iv")
            println("Encrypted Message: $encryptedMessage")
            println("Auth Tag: $authTag")
            EncryptDecrypt.aesGcmDecryptFromBase64FromJsonObject("XHgxl8qs5D6sejZncSsYg7OqPIeV0uQe5I9Zh+uHLcc=",dataObject ).toString()
        }catch (e:Exception){
           Log.d("DEC", "decodeResponse: ${e.message}")
            e.printStackTrace()
            e.message
        }

    }
}
private fun okhttp3.RequestBody.toStringRepresentation(): String {
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}