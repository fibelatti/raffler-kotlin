package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.experimental.Dispatchers
import kotlin.coroutines.experimental.CoroutineContext

class TestThreadProvider : ThreadProvider {
    override fun main(): CoroutineContext = Dispatchers.Unconfined

    override fun background(): CoroutineContext = Dispatchers.Unconfined
}
