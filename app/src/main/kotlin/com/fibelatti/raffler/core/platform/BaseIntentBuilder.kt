package com.fibelatti.raffler.core.platform

import android.content.Context
import android.content.Intent

abstract class BaseIntentBuilder<in T>(context: Context?, clazz: Class<T>) {

    protected val intent: Intent by lazy { Intent(context, clazz) }

    fun build(): Intent = intent
}
