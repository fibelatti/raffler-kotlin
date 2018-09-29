package com.fibelatti.raffler.core.persistence.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListTypeConverter {

    @TypeConverter
    fun fromStringList(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
