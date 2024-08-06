package com.isu.apitracker

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class ApiInterceptor(
    private val context: Context,
    private val listOfDecoder: List<BodyDecoder>? = null,
) : Interceptor {

    // Database initialization
    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    ).build()
    private val dao = db.transactionDataDao()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Create request JSON object
        val requestJson = JsonObject().apply {
            add("headers", Gson().toJsonTree(request.headers.toMultimap()))
            request.body?.let { addProperty("requestbody", it.toStringRepresentation()) }
        }

        val response = chain.proceed(request)
        val responseBodyString = response.body.string()

        // Create response JSON object
        val responseJson = JsonObject().apply {
            addProperty("code", response.code)
            add("headers", Gson().toJsonTree(response.headers.toMultimap()))
            addProperty("responsebody", responseBodyString)
        }


        showNotification(response, request)
        val newResponse = response.newBuilder()
            .apply {
                body(responseBodyString.toResponseBody(response.body.contentType()))
            }
            .build()
        saveTransactionData(request, requestJson, newResponse, responseJson)


        return newResponse
    }


    private fun showNotification(response: Response, request: Request) {
        NotificationHelper(context).showNotification(
            "${response.code} ${request.method} ${request.url}"
        )
    }

    private fun saveTransactionData(
        request: Request,
        requestJson: JsonObject,
        response: Response,
        responseJson: JsonObject,
    ) {
        try {
            var formattedRequestJson = Gson().toJson(requestJson.toRequestData())
            var formattedResponseJson = Gson().toJson(responseJson.toResponseData())
            var decodedRequest = ""
            var decodedResponse = ""
            listOfDecoder?.forEach {
                decodedRequest +=
                        try {
                            it.decodeRequest(request)
                        } catch (e: Exception) {
                            e.message
                            e.printStackTrace()
                        }
                decodedResponse += try {
                    it.decodeResponse(response)
                } catch (e: Exception) {
                    e.message
                    e.printStackTrace()

                }
            }


            val transactionData = TransactionData(
                url = request.url.toString(),
                method = request.method,
                statusCode = response.code.toString(),
                request = formattedRequestJson,
                response = formattedResponseJson,
                time = "",
                decoderRequest = decodedRequest+"\n",
                decoderResponse = decodedResponse+"\n"// Add appropriate timestamp
            )

            dao.insert(transactionData)
        } catch (e: Exception) {
            Log.e("ApiInterceptor", "Error saving transaction data", e)
        }
    }
}

// Extension function to convert request body to string
private fun okhttp3.RequestBody.toStringRepresentation(): String {
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}


// Extension function to convert JSON object to ResponseData
private fun JsonObject.toResponseData(): ResponseData {
    return ResponseData(
        body = if (has("responsebody")) get("responsebody").asString else "",
        code = get("code").asInt,
        headers = Gson().toJson(get("headers"))
    )
}

// Extension function to convert JSON object to RequestData
private fun JsonObject.toRequestData(): RequestData {
    return RequestData(
        body = if (has("requestbody")) get("requestbody").asString else "",
        headers = Gson().toJson(get("headers"))
    )
}
