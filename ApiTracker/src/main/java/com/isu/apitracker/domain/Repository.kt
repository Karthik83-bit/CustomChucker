package com.isu.apitracker.domain

import androidx.lifecycle.LiveData
import com.isu.apitracker.data.model.TransactionData

interface Repository {
    suspend fun getAllApiData(): List<TransactionData>
    suspend fun getAllApiDataPaged(limit: Int, offset: Int): List<TransactionData>
    suspend fun deleteAllApiData()
    suspend fun deleteApiDataWithIds(id:List<Int>)

}