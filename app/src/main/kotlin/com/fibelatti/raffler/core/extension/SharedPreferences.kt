package com.fibelatti.raffler.core.extension

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

// region getSharedPreferences
fun Context.getSharedPreferences(name: String): SharedPreferences =
    getSharedPreferences(name, MODE_PRIVATE)

fun Context.getUserPreferences() = getSharedPreferences("user_preferences")
// endregion

// region Operations on SharedPreferences
private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    operation(editor)
    editor.apply()
}

fun SharedPreferences.put(key: String, value: Any?) {
    when (value) {
        is Boolean -> edit { it.putBoolean(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        is String? -> edit { it.putString(key, value) }
    }
}

fun SharedPreferences.clear() {
    edit { it.clear() }
}

inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? =
    when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as? T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as? T?
        Float::class -> getFloat(key, defaultValue as Float? ?: -1f) as? T?
        Long::class -> getLong(key, defaultValue as Long? ?: -1L) as? T?
        String::class -> getString(key, defaultValue as String?) as? T?
        else -> throw UnsupportedOperationException("Class not supported by SharedPreferences.put()")
    }
// endregion
