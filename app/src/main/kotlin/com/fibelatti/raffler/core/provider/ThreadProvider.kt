package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.android.Main
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.experimental.CoroutineContext

interface ThreadProvider {
    fun main(): CoroutineContext

    fun background(): CoroutineContext
}

@Singleton
class AppThreadProvider @Inject constructor() : ThreadProvider {
    override fun main(): CoroutineContext = Dispatchers.Main

    override fun background(): CoroutineContext = Dispatchers.IO
}
