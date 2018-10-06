package com.fibelatti.raffler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fibelatti.raffler.core.provider.TestThreadProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Rule
import kotlin.coroutines.CoroutineContext

abstract class BaseTest : CoroutineScope {
    @Suppress("LeakingThis")
    @get:Rule
    val injectMocks = InjectMocksRule.create(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val testThreadProvider: ThreadProvider = TestThreadProvider()

    override val coroutineContext: CoroutineContext
        get() = testThreadProvider.main()

    protected fun start(
        block: suspend CoroutineScope.() -> Unit
    ) = launch(testThreadProvider.main()) { block() }
}
