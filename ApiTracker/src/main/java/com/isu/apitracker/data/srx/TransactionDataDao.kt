package com.isu.apitracker.data.srx

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isu.apitracker.data.model.TransactionData

// Define the DAO
@Dao
interface TransactionDataDao {
    @Query("SELECT * FROM transaction_data")
    suspend fun getAll():List<TransactionData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(transactionData: TransactionData)

    @Query("DELETE FROM transaction_data")
    suspend fun delete()

    @Query("DELETE FROM transaction_data WHERE id IN (:ids)")
    suspend fun deleteUsersByIds(ids: List<Int>)
}