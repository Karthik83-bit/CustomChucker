package com.isu.apitracker.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser.parseString
import com.google.gson.JsonSyntaxException

fun beautifyJson(jsonString: String): String? {
    return try {
        val jsonElement = parseString(jsonString)
        val gson = GsonBuilder().setPrettyPrinting().create()
        gson.toJson(jsonElement)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        // Handle the error here if the JSON string is not valid
        null
    }
}