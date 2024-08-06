//package com.isu.apitracker.presentation
//
//
//import com.google.gson.annotations.SerializedName
//import androidx.annotation.Keep
//import com.isu.apitracker.RequestData
//
//data class TransactionData(
//    val url:String,
//    val method:String,
//    val statusCode:String,
//    val request:RequestData,
//    val response:ResponseData,
//    val time:String,
//    val date:String,
//
//)
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
//
//@Keep
//data class ResponseData(
//    @SerializedName("body")
//    val body: String?, // {"id":41,"quote":"Let the beauty we love be what we do. There are hundreds of ways to kneel and kiss the ground.","author":"Rumi"}
//    @SerializedName("code")
//    val code: Int?, // 200
//    @SerializedName("headers")
//    val headers: Headers?
//)
//{
//    @Keep
//    data class Headers(
//        @SerializedName("access-control-allow-origin")
//        val accessControlAllowOrigin: List<String?>?,
//        @SerializedName("connection")
//        val connection: List<String?>?,
//        @SerializedName("content-length")
//        val contentLength: List<String?>?,
//        @SerializedName("content-type")
//        val contentType: List<String?>?,
//        @SerializedName("date")
//        val date: List<String?>?,
//        @SerializedName("etag")
//        val etag: List<String?>?,
//        @SerializedName("nel")
//        val nel: List<String?>?,
//        @SerializedName("report-to")
//        val reportTo: List<String?>?,
//        @SerializedName("reporting-endpoints")
//        val reportingEndpoints: List<String?>?,
//        @SerializedName("server")
//        val server: List<String?>?,
//        @SerializedName("strict-transport-security")
//        val strictTransportSecurity: List<String?>?,
//        @SerializedName("vary")
//        val vary: List<String?>?,
//        @SerializedName("via")
//        val via: List<String?>?,
//        @SerializedName("x-content-type-options")
//        val xContentTypeOptions: List<String?>?,
//        @SerializedName("x-dns-prefetch-control")
//        val xDnsPrefetchControl: List<String?>?,
//        @SerializedName("x-download-options")
//        val xDownloadOptions: List<String?>?,
//        @SerializedName("x-frame-options")
//        val xFrameOptions: List<String?>?,
//        @SerializedName("x-ratelimit-limit")
//        val xRatelimitLimit: List<String?>?,
//        @SerializedName("x-ratelimit-remaining")
//        val xRatelimitRemaining: List<String?>?,
//        @SerializedName("x-ratelimit-reset")
//        val xRatelimitReset: List<String?>?,
//        @SerializedName("x-xss-protection")
//        val xXssProtection: List<String?>?
//    )
//}