package com.fibelatti.raffler.core.platform

import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

abstract class BaseIntentBuilder<in T : Any>(context: Context?, clazz: KClass<T>) {

    protected val intent: Intent by lazy { Intent(context, clazz.java) }

    fun build(): Intent = intent
}
