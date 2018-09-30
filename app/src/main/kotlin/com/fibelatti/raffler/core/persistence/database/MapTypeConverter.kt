package com.fibelatti.raffler.core.persistence.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface MapTypeConverter<K, V> {
    @TypeConverter
    fun fromMap(map: Map<K, V>): String = Gson().toJson(map)

    @TypeConverter
    fun toMap(value: String): Map<K, V> {
        val mapType = object : TypeToken<Map<K, V>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}

class MapStringBooleanTypeConverter : MapTypeConverter<String, Boolean>
