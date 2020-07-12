package com.fibelatti.raffler.core.provider

import androidx.annotation.StringRes
import com.google.gson.reflect.TypeToken

interface ResourceProvider {

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String

    fun <T> getJsonFromAssets(fileName: String, type: TypeToken<T>): T?
}
