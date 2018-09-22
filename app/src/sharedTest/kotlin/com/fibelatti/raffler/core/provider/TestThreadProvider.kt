package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.experimental.Unconfined
import kotlin.coroutines.experimental.CoroutineContext

class TestThreadProvider : ThreadProvider {
    override fun main(): CoroutineContext = Unconfined

    override fun background(): CoroutineContext = Unconfined
}
