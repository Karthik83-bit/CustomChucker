//package com.isu.apitracker
//
//
//import com.google.gson.annotations.SerializedName
//import androidx.annotation.Keep
//
//@Keep
//data class RequestData(
//    @SerializedName("body")
//    val body: String?, // {"authTag":"e5Ha1ViRe13ict4DBX8xyA\u003d\u003d","encryptedMessage":"D0vg+b1YDD3MH5RvQDTE9lViyoafx7unIvxdJETqxoGaUvWdzXwN/CuD1M7pLZwvwJVUbp1gl2V+\nabd1lOe7sGWb2PqxivW0PCut++tJt7lads+smYVQPkScD9Brsvz8QuSdh2ON+9tuHejqhKD5LeMY\nB9A\u003d","iv":"GWWsjKZDKJyPnrtG"}
//    @SerializedName("headers")
//    val headers: Headers?
//) {
//    @Keep
//    data class Headers(
//        @SerializedName("authorization")
//        val authorization: List<String?>?
//    )
//}