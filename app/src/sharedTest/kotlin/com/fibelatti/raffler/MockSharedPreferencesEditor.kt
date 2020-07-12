package com.fibelatti.raffler

import android.content.SharedPreferences

open class MockSharedPreferencesEditor : SharedPreferences.Editor {

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this

    override fun putString(key: String?, value: String?): SharedPreferences.Editor = this

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = this

    override fun remove(key: String?): SharedPreferences.Editor = this

    override fun clear(): SharedPreferences.Editor = this

    override fun commit(): Boolean = true

    override fun apply() {
        // Intentionally empty
    }
}
