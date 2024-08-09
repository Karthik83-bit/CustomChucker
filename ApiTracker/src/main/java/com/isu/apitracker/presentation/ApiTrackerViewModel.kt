package com.isu.apitracker.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.isu.apitracker.domain.Repository
import com.isu.apitracker.presentation.screens.ApiListDataClass
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * ViewModel class to manage API tracking data.
 *
 * @property repository Repository instance to interact with data layer.
 */
class ApiTrackerViewModel(val repository: Repository) : ViewModel() {

    var selectedApi: MutableState<ApiListDataClass?> = mutableStateOf(null)
    val apiList = mutableStateListOf<ApiListDataClass>()
    val selectedApiToDelete = mutableStateListOf<Int>()
    val requestHeaders = mutableMapOf<String, String>()
    val responseHeaders = mutableMapOf<String, String>()

    /**
     * Fetches all API data and updates the UI list.
     */
    fun getAllApi() {
        viewModelScope.launch {
            val list = repository.getAllApiData()
            Log.d("apiData", "getAllApi: $list")
            var reqHeader = ""
            var respHeader = ""
            var reqBody = ""
            var respBody = ""
            var date = ""
            val uiList = list.map {
                try {
                    val requestObj = JSONObject(it.request)
                    val responseObj = JSONObject(it.response)
                    reqHeader = requestObj.getString("headers")
                    respHeader = responseObj.getString("headers")

                    JSONObject(reqHeader).keys().forEach { key ->
                        requestHeaders[key] = JSONObject(reqHeader).get(key).toString()
                    }
                    JSONObject(respHeader).keys().forEach { key ->
                        responseHeaders[key] = JSONObject(respHeader).get(key).toString()
                    }
                    date = getCurrentDateWithoutTime()
                    reqBody = processJson(Gson().toJson(requestObj.getString("body")))
                    respBody = processJson(Gson().toJson(responseObj.getString("body")))

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("kerr", "getAllApi: ${e.message}")
                }

                ApiListDataClass(
                    id = it.id,
                    statusCode = it.statusCode.toString(),
                    requestMethod = it.method,
                    requestEndPoint = it.url,
                    requestBaseURL = it.url,
                    callTime = it.recordTime,
                    startTime=date,
                    responseTime = it.time,
                    memoryConsumption = 9803,
                    request = reqBody,
                    response = respBody,
                    responseHeaders = responseHeaders,
                    requestHeaders = requestHeaders,
                    decodedRequest = it.decoderRequest,
                    decodedOutput = it.decoderResponse
                )
            }
            if (apiList.isEmpty()) {
                apiList.addAll(uiList)
            }
        }
    }
    /**
     * This function formats the current date without including the time.
     * @return A string representing the current date in the format "EEE, dd MMM yyyy"
     */
    fun getCurrentDateWithoutTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault() // Ensure it uses the local time zone
        return dateFormat.format(date)
    }

    /**
     * Processes JSON string to make it more readable.
     *
     * @param str The JSON string to process.
     * @return The processed JSON string.
     */
    fun processJson(str: String): String {
        return str.replace("""\\\"""", "")
            .replace("""\"""", "\"")
            .split("{")
            .joinToString("{\n ")
            .split(",")
            .joinToString(",\n")
            .split("}")
            .joinToString("\n}\n")
    }

    /**
     * Deletes the selected API data from the repository and updates the UI list.
     */
    fun deleteSelectedApi() {
        viewModelScope.launch {
            if (selectedApiToDelete.isNotEmpty()) {
                repository.deleteApiDataWithIds(selectedApiToDelete)
                selectedApiToDelete.forEach { id ->
                    apiList.removeAll { it.id == id }
                }
            }
        }
    }

    /**
     * Deletes all API data from the repository and clears the UI list.
     */
    fun deleteAllApi() {
        viewModelScope.launch {
            repository.deleteAllApiData()
            apiList.clear()
        }
    }
}
