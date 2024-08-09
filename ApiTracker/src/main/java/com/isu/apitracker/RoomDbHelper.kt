package com.isu.apitracker
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

data class RequestData(
    @ColumnInfo(name = "requestbody")
    val body: String?, // {"authTag":"e5Ha1ViRe13ict4DBX8xyA\u003d\u003d","encryptedMessage":"D0vg+b1YDD3MH5RvQDTE9lViyoafx7unIvxdJETqxoGaUvWdzXwN/CuD1M7pLZwvwJVUbp1gl2V+\nabd1lOe7sGWb2PqxivW0PCut++tJt7lads+smYVQPkScD9Brsvz8QuSdh2ON+9tuHejqhKD5LeMY\nB9A\u003d","iv":"GWWsjKZDKJyPnrtG"}

    @ColumnInfo(name = "requestheaders")
    val headers: String?
)

data class ResponseData(
    @ColumnInfo(name = "responsebody")
    val body: String?, // {"id":41,"quote":"Let the beauty we love be what we do. There are hundreds of ways to kneel and kiss the ground.","author":"Rumi"}

    @ColumnInfo(name = "code")
    val code: Int?, // 200

    @ColumnInfo(name = "responseheaders")
    val headers: String?
)


class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String?>? {
        val listType = object : TypeToken<List<String?>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromListString(list: List<String?>?): String {
        return gson.toJson(list)
    }
}

// Add this to your Room database class
@Database(entities = [TransactionData::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDataDao(): TransactionDataDao
}

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
