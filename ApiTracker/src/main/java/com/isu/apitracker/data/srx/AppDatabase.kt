package com.isu.apitracker.data.srx

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isu.apitracker.data.model.TransactionData

// Add this to your Room database class
@Database(entities = [TransactionData::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDataDao(): TransactionDataDao
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