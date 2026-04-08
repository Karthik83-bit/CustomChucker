package com.isu.apitracker.data.srx

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isu.apitracker.data.model.TransactionData

// Add this to your Room database class
@Database(entities = [TransactionData::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDataDao(): TransactionDataDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val existingColumns = buildSet {
                    database.query("PRAGMA table_info(transaction_data)").use { cursor ->
                        val nameIndex = cursor.getColumnIndex("name")
                        while (cursor.moveToNext()) {
                            if (nameIndex != -1) {
                                add(cursor.getString(nameIndex))
                            }
                        }
                    }
                }

                if ("request_file_path" !in existingColumns) {
                    database.execSQL(
                        "ALTER TABLE transaction_data ADD COLUMN request_file_path TEXT"
                    )
                }

                if ("response_file_path" !in existingColumns) {
                    database.execSQL(
                        "ALTER TABLE transaction_data ADD COLUMN response_file_path TEXT"
                    )
                }
            }
        }
    }
}




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


    @TypeConverter
    fun fromMap(value: Map<String, List<String>>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String): Map<String, List<String>> {
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        return gson.fromJson(value, mapType)
    }
}
