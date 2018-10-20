package com.fibelatti.raffler.core.persistence.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapStringIntTypeConverter {
    @TypeConverter
    fun fromMap(map: Map<String, Int>): String = Gson().toJson(map)

    @TypeConverter
    fun toMap(value: String): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type

        return try {
            Gson().fromJson(value, mapType)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
