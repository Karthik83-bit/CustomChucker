package com.isu.apitracker

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.isu.apitracker.domain.Repository
import com.isu.apitracker.presentation.screens.ApiListDataClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ApiTrackerViewModel @Inject constructor(val repository: Repository):ViewModel() {

    var selectedApi: MutableState<ApiListDataClass?> = mutableStateOf(null)
    val apiList= mutableStateListOf<ApiListDataClass>()
    val selectedApiToDelete= mutableStateListOf<Int>()
    fun getAllApi(){
        viewModelScope.launch {
            val list=repository.getAllApiData()
            Log.d("apiData", "getAllApi: $list")
            var reqHeader=""
            var respHeader=""
            var reqBody=""
            var respBody=""
            var date=""
            var timeTaken=""
           val uiList= list.map {
               try{
                   val requestObj=JSONObject(it.request)
                   val responseObj=JSONObject(it.response)
                   reqHeader=requestObj.getString("headers")
                   respHeader=responseObj.getString("headers")
                   date=JSONObject(responseObj.getString("headers")).getJSONArray("date")[0].toString()

                   reqBody=proccessJson(Gson().toJson(requestObj.getString("body")))
                   respBody=proccessJson(Gson().toJson(responseObj.getString("body")))


               }catch (e:Exception){
                   e.printStackTrace()
                   Log.d("kerr", "getAllApi: ${e.message}")
               }


                ApiListDataClass(
                    id=it.id,
                    statusCode = it.statusCode.toString(),
                    requestMethod = it.method,
                    requestEndPoint = it.url,
                    requestBaseURL = it.time,
                    callTime = date,
                    responseTime = 2215,
                    memoryConsumption = 9803,
                    request = reqBody,
                    response = respBody,
                    responseHeaders = respHeader,
                    requestHeaders = reqHeader,
                    decodedRequest = it.decoderRequest,
                    decodedOutput=it.decoderResponse


                )
            }
            if(apiList.isEmpty()){
                apiList.addAll(uiList)
            }


        }


    }

    private fun proccessJson(str: String): String {
        return str.replace("""\""","")
            .replace("\"\"","")

            .split("{")
            .joinToString("{\n ")
            .split(",")
            .joinToString(",\n")
            .split("}")
            .joinToString ("\n}\n")
    }

    fun deleteSelectedApi() {
       viewModelScope.launch {
           if(selectedApiToDelete!=null){
               repository.deleteApiDataWithIds(selectedApiToDelete)
               selectedApiToDelete.forEach{id->
                   apiList.removeIf { it.id==id}
               }
           }

       }
    }

    fun deleteAllApi() {
        viewModelScope.launch {
            repository.deleteAllApiData()
            apiList.clear()
        }
    }


}