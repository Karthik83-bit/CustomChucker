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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private val listOfEcludedUrlForDEcoding:List<String>?=null,
    private val maxContentLength: Long = 1024 * 1024 // 1MB default limit
) : Interceptor {
    // Keep SQLite rows small; larger payloads are stored as files and referenced by path.
    private val maxInlineStorageLength: Long = 100 * 1024

    // Database initialization
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database"
    )
        .addMigrations(AppDatabase.MIGRATION_3_4)
        .build()
    private val dao = db.transactionDataDao()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()

        // Create request JSON object

        val requestHeaders=request.headers.toMultimap()
        val requestString=request.body?.toStringRepresentation()
        val response = chain.proceed(request)
        val endTime = System.nanoTime()
        val responseHeaders=response.headers.toMultimap()
        val responseBodyString = response.body?.string()
        //to avoid consumption of interceptor

        val newResponseToReturn = response.newBuilder()
            .headers(response.headers)
            .code(response.code)
            .body(responseBodyString?.toResponseBody(response.body?.contentType()))

            .build()
        val newResponse = response.newBuilder()
            .headers(response.headers)
            .code(response.code)
            .body(responseBodyString?.toResponseBody(response.body?.contentType()))
            .build()
        val duration = (endTime - startTime) / 1_000_000.0
        val startTimeString = getCurrentTime()
        val responseFilePath = saveTransactionData(
            request=request,
            response=newResponse,
            duration = duration,
            startTimeString = startTimeString,
            requestBody = requestString ?: "",
            requestHeaders = requestHeaders,
            responseBody = responseBodyString?:"",
            responseHeaders = responseHeaders,
            listOfEcludedUrlForDEcoding
        )
        showNotification(response, request, responseFilePath)

        return newResponseToReturn
    }

    /**
     * Shows a notification with the API response code, method, and URL.
     *
     * @param response the API response
     * @param request the API request
     */
    private fun showNotification(response: Response, request: Request, responseFilePath: String? = null) {
        NotificationHelper(context).showNotification(
            "${response.code} ${request.method} ${request.url}",
            responseFilePath
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


    ): String? {
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
            }else{
                try {
                    decodedRequest.addAll(listOfDecoder?.map { it.decodeRequest(request) } ?: emptyList())
                    decodedResponse.addAll(listOfDecoder?.map { it.decodeResponse(response) } ?: emptyList())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Handle large bodies: if over limit, save to file
            val (finalRequestBody, requestFilePath) = handleLargeContent(requestBody, "request_${System.currentTimeMillis()}", maxContentLength)
            val (finalResponseBody, responseFilePath) = handleLargeContent(
                content = responseBody,
                fileName = "response_${System.currentTimeMillis()}",
                maxLength = maxContentLength
            )

            val transactionData = TransactionData(
                url = request.url.toString(),
                method = request.method,
                statusCode = response.code.toString(),
                request = finalRequestBody,
                response = finalResponseBody,
                time = duration.toString(),
                decoderRequest = decodedRequest,
                recordTime = startTimeString,
                decoderResponse = decodedResponse,
                requestHeaders =requestHeaders,
                responseHeaders =responseHeaders,
                requestFilePath = requestFilePath,
                responseFilePath = responseFilePath,

            )

            // Use coroutine to avoid blocking main thread
            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                dao.insert(transactionData)
            }
            return responseFilePath
        } catch (e: Exception) {
            Log.e("ApiInterceptor", "Error saving transaction data", e)
            return null
        }
    }

    private fun truncateIfNeeded(content: String, maxLength: Long): String {
        return if (content.length > maxLength) {
            content.substring(0, maxLength.toInt()) + "\n\n[Content truncated due to size limit]"
        } else {
            content
        }
    }

    private fun handleLargeContent(
        content: String,
        fileName: String,
        maxLength: Long
    ): Pair<String, String?> {
        val inlineLimit = minOf(maxLength, maxInlineStorageLength)
        return if (content.length > inlineLimit) {
            // Save to file
            val filePath = saveContentToFile(content, fileName)
            Pair("[Content saved to file: $filePath]", filePath)
        } else {
            Pair(content, null)
        }
    }

    private fun saveContentToFile(content: String, fileName: String): String {
        val file = java.io.File(context.getExternalFilesDir("api_logs"), "$fileName.txt")
        file.parentFile?.mkdirs()
        file.writeText(content)
        return file.absolutePath
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



