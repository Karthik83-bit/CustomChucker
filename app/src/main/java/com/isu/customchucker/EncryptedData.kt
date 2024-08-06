package com.isu.customchucker

import com.google.gson.annotations.SerializedName

data class EncryptedData(
        var authTag: String?=null,
        var encryptedMessage: String?=null,
        var iv: String?=null
)

data class EncryptedResponse(
        @SerializedName("data")
        val `data`: EncryptedData,
        @SerializedName("status")
        val status: String?, // SUCCESS
        @SerializedName("statusCode")
        val statusCode: Int?, // 0
        @SerializedName("statusDesc")
        val statusDesc: String? // OTP G
)