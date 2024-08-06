package com.isu.customchucker


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class QuoteResponse(
    @SerializedName("author")
    val author: String?, // Simone Weil
    @SerializedName("authorSlug")
    val authorSlug: String?, // simone-weil
    @SerializedName("content")
    val content: String?, // Liberty, taking the word in its concrete sense, consists in the ability to choose.
    @SerializedName("dateAdded")
    val dateAdded: String?, // 2019-12-23
    @SerializedName("dateModified")
    val dateModified: String?, // 2023-04-14
    @SerializedName("_id")
    val id: String?, // OSF3eMB6sZaP
    @SerializedName("length")
    val length: Int?, // 82
    @SerializedName("tags")
    val tags: List<String?>?
)