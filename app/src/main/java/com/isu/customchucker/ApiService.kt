package com.isu.customchucker

import android.content.Context
import com.isu.apitracker.presentation.ApiInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import kotlin.random.Random

interface ApiService {
    companion object{


        fun getService(context: Context): ApiService {
            return Retrofit
                .Builder()
                .baseUrl("https://prepaidcard-gateway.iserveu.online")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient(context))
                .build()
                .create(ApiService::class.java)
        }

        private fun getClient(context: Context): OkHttpClient {
            val client=OkHttpClient
                .Builder()
                .addInterceptor(ApiInterceptor(context=context, listOfDecoder = listOf(EncryptDecryptDecoder())))
                .build()
            return client
        }


    }
    @GET()
    suspend fun getApiResponse(@Url url:String="https://dummyjson.com/quotes/${Random.nextInt(1,200)}"):Response<NewQuoteResponse>

    @POST("/card/generate_otp")
    suspend fun generateOTP(@Body request: EncryptedData,
                            @Header("Authorization") token: String="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyZWRpcmVjdFVyaSI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDU2OC8jL3YxL2Rhc2hib2FyZC9hbmFseXRpY3MiLCJiYW5rQ29kZSI6ImZpbm8iLCJwcml2aWxlZ2VzIjpbIjUwMCIsIjUwNCIsIjE5IiwiMTgiXSwiaXMyRkFFbmFibGVkIjp0cnVlLCJ1c2VyX25hbWUiOiJDVVNUODQ1NjgzNTAwMiIsIm1vYmlsZU51bWJlciI6ODQ1NjgzNTAwMiwiY3JlYXRlZCI6MTcyMjkxNjE2NjE1OSwiaXNCaW9BdXRoUmVxdWlyZWQiOnRydWUsInBhcmVudFVzZXJOYW1lIjoiQ09SUDk4NTM1MzcwODYiLCJhdXRob3JpdGllcyI6WyJST0xFX1JFVEFJTEVSIl0sImNsaWVudF9pZCI6ImlzdS1maW5vLWNsaWVudCIsImFkbWluTmFtZSI6ImZpbm9fYWRtaW4iLCJpc1Bhc3N3b3JkUmVzZXRSZXF1aXJlZCI6ZmFsc2UsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE3MjI5MTc5NjYsImp0aSI6ImRPak1UMFZpTEFpekpVbl9EQkI5Z3RBcW5SZyJ9.jD37hdpsf7CM1h9eYMIOdW7PpPWDqSvzqLVrEKeOck5YMMfq7w_6uwg1llTbId3AHYoEHKPePg8_imYCfSqfovSskUReQa7KmSgbGpN-o5Ew9UWIcNct4P-6c3YDUe8Jz21TbNGbNabh2eE8cNyWSbA6Y0-D8CiKh_i--vmtMOWbACdqgM6poY_R6BP3Yh6gDIplIqc3VUpgHt3AQyAzIWtzDcvtKJvqjZ1gqj-K_rqdWCMDPDkKy3X4MXor5crGfbr1ZCSTwQaTocY2NSO7fDydlJ4_KNpFGZD3zVFZ2CyiMfnYF33b3g5lvjerTCguzzg1o3SX9cfeqXrynirAkg"): Response<EncryptedResponse>

}
