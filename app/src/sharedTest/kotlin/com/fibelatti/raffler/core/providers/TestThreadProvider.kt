package com.fibelatti.raffler.core.providers

import kotlinx.coroutines.experimental.Unconfined
import kotlin.coroutines.experimental.CoroutineContext

class TestThreadProvider : ThreadProvider {
    override fun main(): CoroutineContext = Unconfined

    override fun background(): CoroutineContext = Unconfined
}
