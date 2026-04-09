package com.isu.customchucker

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.isu.apitracker.util.toEm
import com.isu.customchucker.ui.theme.CustomChuckerTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val requestNotificationPermission= rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {

            }
            CustomChuckerTheme {
                LaunchedEffect(key1 = Unit) {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val text:MutableState<String> = remember {
                        mutableStateOf("")
                    }
                    val scope = rememberCoroutineScope()
                  Column(
                      Modifier
                          .fillMaxSize()
                          .padding(innerPadding)
                          .padding(16.dp)
                          .verticalScroll(rememberScrollState()),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center) {
                      Text(lineHeight=16.sp.toEm(), text = "API Tracker Test Cases")
                      Spacer(modifier = Modifier.height(16.dp))
                      if(text.value.isEmpty()){
                          CircularProgressIndicator()
                      }else{
                          Text(lineHeight=16.sp.toEm(),text=text.value)
                      }
                      Spacer(modifier = Modifier.height(16.dp))
                      Button(onClick = {
                          callApi(onLoading = {},onSucess = {
                              text.value=it.toString()
                          }, onError = {
                              text.value=it
                          })
                      }) {
                          Text(lineHeight=14.sp.toEm(), text = "OTP Test")
                      }
                      Spacer(modifier = Modifier.height(12.dp))
                      Button(onClick = {
                          scope.launch {
                              text.value = "Calling normal success API..."
                              text.value = callRawApi("https://dummyjson.com/quotes/1")
                          }
                      }) {
                          Text(lineHeight=14.sp.toEm(), text = "Small Success")
                      }
                      Spacer(modifier = Modifier.height(12.dp))
                      Button(onClick = {
                          scope.launch {
                              text.value = "Calling small 404 API..."
                              text.value = callRawApi("https://dummyjson.com/quotes/999999")
                          }
                      }) {
                          Text(lineHeight=14.sp.toEm(), text = "Small 404")
                      }
                      Spacer(modifier = Modifier.height(12.dp))
                      Button(onClick = {
                          scope.launch {
                              text.value = "Calling large payload API..."
                              text.value = callRawApi("https://httpbin.org/bytes/180000")
                          }
                      }) {
                          Text(lineHeight=14.sp.toEm(), text = "Large Blob 200")
                      }
                      Spacer(modifier = Modifier.height(12.dp))
                      Button(onClick = {
                          scope.launch {
                              text.value = "Calling delayed 404 flow..."
                              delay(500)
                              text.value = callRawApi("https://dummyjson.com/kitees/999999")
                          }
                      }) {
                          Text(lineHeight=14.sp.toEm(), text = "404 Notification Test")
                      }
                  }
                }
            }
        }
    }

    private fun callApi(onLoading:(Boolean)->Unit,onSucess:(EncryptedResponse)->Unit,onError:(String)->Unit) {
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        val response= kotlinx.coroutines.GlobalScope. launch(Dispatchers.IO+coroutineExceptionHandler) {
            val service=ApiService.getService(context = applicationContext)
            val request=OtpGenerationRequestModel(
                params = "lkjlkejl",
                userName = "CUST7008656872"
            )
            val accessToken="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyZWRpcmVjdFVyaSI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDU2OC8jL3YxL2Rhc2hib2FyZC9hbmFseXRpY3MiLCJiYW5rQ29kZSI6ImZpbm8iLCJwcml2aWxlZ2VzIjpbIjUwMCIsIjUwNCIsIjE5IiwiMTgiXSwiaXMyRkFFbmFibGVkIjp0cnVlLCJ1c2VyX25hbWUiOiJDVVNUODQ1NjgzNTAwMiIsIm1vYmlsZU51bWJlciI6ODQ1NjgzNTAwMiwiY3JlYXRlZCI6MTcyMjkxNjE2NjE1OSwiaXNCaW9BdXRoUmVxdWlyZWQiOnRydWUsInBhcmVudFVzZXJOYW1lIjoiQ09SUDk4NTM1MzcwODYiLCJhdXRob3JpdGllcyI6WyJST0xFX1JFVEFJTEVSIl0sImNsaWVudF9pZCI6ImlzdS1maW5vLWNsaWVudCIsImFkbWluTmFtZSI6ImZpbm9fYWRtaW4iLCJpc1Bhc3N3b3JkUmVzZXRSZXF1aXJlZCI6ZmFsc2UsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE3MjI5MTc5NjYsImp0aSI6ImRPak1UMFZpTEFpekpVbl9EQkI5Z3RBcW5SZyJ9.jD37hdpsf7CM1h9eYMIOdW7PpPWDqSvzqLVrEKeOck5YMMfq7w_6uwg1llTbId3AHYoEHKPePg8_imYCfSqfovSskUReQa7KmSgbGpN-o5Ew9UWIcNct4P-6c3YDUe8Jz21TbNGbNabh2eE8cNyWSbA6Y0-D8CiKh_i--vmtMOWbACdqgM6poY_R6BP3Yh6gDIplIqc3VUpgHt3AQyAzIWtzDcvtKJvqjZ1gqj-K_rqdWCMDPDkKy3X4MXor5crGfbr1ZCSTwQaTocY2NSO7fDydlJ4_KNpFGZD3zVFZ2CyiMfnYF33b3g5lvjerTCguzzg1o3SX9cfeqXrynirAkg"
            val encryptedRequest=try {
                EncryptDecrypt.aesGcmEncryptToEncryptedDataClass(data = Gson().toJson(request))
            } catch (e:Exception){
               e.printStackTrace()
                null
            }
            val response=service.generateOTP(encryptedRequest?:EncryptedData())
            if(response.isSuccessful){
                if(response.body()!=null){
                    onSucess(response.body()!!)
                }
                else{
                    onError(response.message()+response.errorBody().toString())
                }

            }else{
                onError(response.message())
            }
        }
        onLoading(response.isActive)


    }

    private suspend fun callRawApi(url: String): String {
        return try {
            val service = ApiService.getService(applicationContext)
            val response = service.getRawResponse(url)
            val bodyPreview = response.errorBody()?.string()
                ?: response.body()?.string()
                ?: "No body"
            val compactPreview = bodyPreview.take(200)
            "Code: ${response.code()}\nUrl: $url\nBody preview: $compactPreview"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(lineHeight=16.sp.toEm(),
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CustomChuckerTheme {
        Greeting("Android")
    }
}
