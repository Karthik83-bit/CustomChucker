package com.isu.apitracker.presentation

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.isu.apitracker.util.BodyDecoder
import com.isu.apitracker.util.NotificationHelper
import com.isu.apitracker.data.model.TransactionData
import com.isu.apitracker.data.srx.AppDatabase
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Interceptor class for tracking API requests and responses.
 *
 * @property context the application context
 * @property listOfDecoder the list of body decoders for processing request and response bodies
 */
class ApiInterceptor(
    private val context: Context,
    private val listOfDecoder: List<BodyDecoder>? = null,
    private val listOfEcludedUrlForDEcoding:List<String>?=null
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
        val startTime = System.nanoTime()

        // Create request JSON object

        val requestHeaders=request.headers.toMultimap()
        val requestString=request.body?.toStringRepresentation()
        Log.d("gsonreq", "intercept: ${request.body?.toStringRepresentation()}")
        Log.d("gsonreq", "intercept: ${request.body?.toStringRepresentation()}")
        val response = chain.proceed(request)
        val endTime = System.nanoTime()
        val responseHeaders=response.headers.toMultimap()
        val responseBodyString = response.body.string()
        showNotification(response, request)
        val newResponse = response.newBuilder()
            .body(responseBodyString.toResponseBody(response.body.contentType()))
            .build()
        val duration = (endTime - startTime) / 1_000_000.0
        val startTimeString = getCurrentTime()
        saveTransactionData(
            request=request,
            response=newResponse,
            duration = duration,
            startTimeString = startTimeString,
            requestBody = requestString ?: "",
            requestHeaders = requestHeaders,
            responseBody = responseBodyString,
            responseHeaders = responseHeaders,
            listOfEcludedUrlForDEcoding
        )

        return newResponse
    }

    /**
     * Shows a notification with the API response code, method, and URL.
     *
     * @param response the API response
     * @param request the API request
     */
    private fun showNotification(response: Response, request: Request) {
        NotificationHelper(context).showNotification(
            "${response.code} ${request.method} ${request.url}"
        )
    }

    /**
     * Gets the current time formatted as HH:mm:ss.
     *
     * @return the current time as a string
     */
    private fun getCurrentTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault() // Ensure it uses the local time zone
        return dateFormat.format(date)
    }

    /**
     * Saves the transaction data to the database.
     *
     * @param request the API request
     * @param requestJson the JSON representation of the request
     * @param response the API response
     * @param responseJson the JSON representation of the response
     * @param duration the duration of the API call in milliseconds
     * @param startTimeString the start time of the API call
     */
    private fun saveTransactionData(
        request: Request,
        response: Response,
        duration: Double,
        startTimeString: String,
        requestBody: String,
        requestHeaders: Map<String, List<String>>,
        responseBody: String,
        responseHeaders: Map<String, List<String>>,
        listOfEcludedUrlForDEcoding: List<String>?


    ) {
        try {
            val decodedRequest: MutableList<String?> = mutableListOf()
            val decodedResponse: MutableList<String?> = mutableListOf()
            if(listOfEcludedUrlForDEcoding!=null){
                if(!listOfEcludedUrlForDEcoding.contains(request.url.toString())){
                    try {
                        decodedRequest.addAll(listOfDecoder?.map { it.decodeRequest(request) } ?: emptyList())
                        decodedResponse.addAll(listOfDecoder?.map { it.decodeResponse(response) } ?: emptyList())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }



            val transactionData = TransactionData(
                url = request.url.toString(),
                method = request.method,
                statusCode = response.code.toString(),
                request = requestBody,
                response = responseBody,
                time = duration.toString(),
                decoderRequest = decodedRequest,
                recordTime = startTimeString,
                decoderResponse = decodedResponse,
                requestHeaders =requestHeaders,
                responseHeaders =responseHeaders,

            )

            dao.insert(transactionData)
        } catch (e: Exception) {
            Log.e("ApiInterceptor", "Error saving transaction data", e)
        }
    }
}



/**
 * Extension function to convert request body to string.
 */
private fun okhttp3.RequestBody.toStringRepresentation(): String {
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}



