package com.isu.customchucker


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OtpGenerationRequestModel(
    @SerializedName("channel")
    val channel: String?="ANDROID", // ANDROID
    @SerializedName("expiryTime")
    val expiryTime: String="10", // 29
    @SerializedName("params")
    val params: String?, // AB5374j2
    @SerializedName("type")
    val type: String?="CUSTOMER", // CUSTOMER
    @SerializedName("userName")
    val userName: String? // CUST9337764181
)