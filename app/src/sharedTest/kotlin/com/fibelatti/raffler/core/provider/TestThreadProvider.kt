package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestThreadProvider : ThreadProvider {
    override fun main(): CoroutineContext = Dispatchers.Unconfined

    override fun background(): CoroutineContext = Dispatchers.Unconfined
}
