package com.isu.apitracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_data")
data class TransactionData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "method")
    val method: String,

    @ColumnInfo(name = "status_code")
    val statusCode: String,

    @ColumnInfo(name = "request")
    val request: String,

    @ColumnInfo(name = "request_headers")
    val requestHeaders: Map<String,List<String>>,

    @ColumnInfo(name = "response_headers")
    val responseHeaders:  Map<String,List<String>>,

    @ColumnInfo(name = "response")
    val response: String,

    @ColumnInfo(name = "recordtime")
    val recordTime: String,

    @ColumnInfo(name = "starttime")
    val time: String,

    @ColumnInfo(name = "decoderRequest")
    val decoderRequest: List<String?>,

    @ColumnInfo(name = "decoderResponse")
    val decoderResponse: List<String?>,


)