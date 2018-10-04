package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.android.Main
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

interface ThreadProvider {
    fun main(): CoroutineContext

    fun background(): CoroutineContext
}

@Singleton
class AppThreadProvider @Inject constructor() : ThreadProvider {
    override fun main(): CoroutineContext = Dispatchers.Main

    override fun background(): CoroutineContext = Dispatchers.IO
}
