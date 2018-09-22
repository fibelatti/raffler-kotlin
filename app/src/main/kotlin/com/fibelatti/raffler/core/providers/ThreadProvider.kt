package com.fibelatti.raffler.core.providers

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.experimental.CoroutineContext

interface ThreadProvider {
    fun main(): CoroutineContext

    fun background(): CoroutineContext
}

@Singleton
class AppThreadProvider @Inject constructor() : ThreadProvider {
    override fun main(): CoroutineContext = UI

    override fun background(): CoroutineContext = CommonPool
}
