package com.fibelatti.raffler.core.platform

import android.content.Intent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

sealed class IntentDelegate<T>(protected val key: kotlin.String) : ReadWriteProperty<Intent, T> {

    class Int(key: kotlin.String, private val defaultValue: kotlin.Int) : IntentDelegate<kotlin.Int>(key) {
        override fun getValue(thisRef: Intent, property: KProperty<*>) = thisRef.getIntExtra(key, defaultValue)

        override fun setValue(thisRef: Intent, property: KProperty<*>, value: kotlin.Int) {
            thisRef.putExtra(key, value)
        }
    }

    class String(key: kotlin.String) : IntentDelegate<kotlin.String>(key) {
        override fun getValue(thisRef: Intent, property: KProperty<*>): kotlin.String = thisRef.getStringExtra(key)

        override fun setValue(thisRef: Intent, property: KProperty<*>, value: kotlin.String) {
            thisRef.putExtra(key, value)
        }
    }

    class Boolean(key: kotlin.String, private val defaultValue: kotlin.Boolean) : IntentDelegate<kotlin.Boolean>(key) {
        override fun getValue(thisRef: Intent, property: KProperty<*>): kotlin.Boolean = thisRef.getBooleanExtra(key, defaultValue)

        override fun setValue(thisRef: Intent, property: KProperty<*>, value: kotlin.Boolean) {
            thisRef.putExtra(key, value)
        }
    }

    class Serializable<T : java.io.Serializable>(key: kotlin.String) : IntentDelegate<T?>(key) {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Intent, property: KProperty<*>): T? = thisRef.getSerializableExtra(key) as? T

        override fun setValue(thisRef: Intent, property: KProperty<*>, value: T?) {
            thisRef.putExtra(key, value)
        }
    }

    class Parcelable<T : android.os.Parcelable>(key: kotlin.String) : IntentDelegate<T>(key) {
        override fun getValue(thisRef: Intent, property: KProperty<*>): T = thisRef.getParcelableExtra(key)

        override fun setValue(thisRef: Intent, property: KProperty<*>, value: T) {
            thisRef.putExtra(key, value)
        }
    }
}
