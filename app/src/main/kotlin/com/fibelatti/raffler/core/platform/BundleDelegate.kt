package com.fibelatti.raffler.core.platform

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

sealed class BundleDelegate<T>(protected val key: kotlin.String) : ReadWriteProperty<Bundle, T> {

    class Int(key: kotlin.String) : BundleDelegate<kotlin.Int>(key) {
        override fun getValue(thisRef: Bundle, property: KProperty<*>) = thisRef.getInt(key)

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: kotlin.Int) {
            thisRef.putInt(key, value)
        }
    }

    class String(key: kotlin.String) : BundleDelegate<kotlin.String>(key) {
        override fun getValue(thisRef: Bundle, property: KProperty<*>): kotlin.String = thisRef.getString(key)

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: kotlin.String) {
            thisRef.putString(key, value)
        }
    }

    class Boolean(key: kotlin.String, private val defaultValue: kotlin.Boolean) : BundleDelegate<kotlin.Boolean>(key) {
        override fun getValue(thisRef: Bundle, property: KProperty<*>): kotlin.Boolean = thisRef.getBoolean(key, defaultValue)

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: kotlin.Boolean) {
            thisRef.putBoolean(key, value)
        }
    }

    class Serializable<T : java.io.Serializable>(key: kotlin.String) : BundleDelegate<T?>(key) {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Bundle, property: KProperty<*>): T? = thisRef.getSerializable(key) as? T

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: T?) {
            thisRef.putSerializable(key, value)
        }
    }

    class Parcelable<T : android.os.Parcelable>(key: kotlin.String) : BundleDelegate<T>(key) {
        override fun getValue(thisRef: Bundle, property: KProperty<*>): T = thisRef.getParcelable(key)

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: T) {
            thisRef.putParcelable(key, value)
        }
    }
}
