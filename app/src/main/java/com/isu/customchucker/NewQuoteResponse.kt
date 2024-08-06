package com.isu.customchucker


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NewQuoteResponse(
    @SerializedName("author")
    val author: String?, // Rumi
    @SerializedName("id")
    val id: Int?, // 1
    @SerializedName("quote")
    val quote: String? // Your heart is the size of an ocean. Go find yourself in its hidden depths.
)