package com.fibelatti.raffler.core.providers

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

interface ThreadProvider {
    fun main(): CoroutineContext

    fun background(): CoroutineContext
}

class AppThreadProvider : ThreadProvider {
    override fun main(): CoroutineContext = UI

    override fun background(): CoroutineContext = CommonPool
}
