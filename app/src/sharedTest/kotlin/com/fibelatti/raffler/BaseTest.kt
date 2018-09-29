package com.fibelatti.raffler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fibelatti.raffler.core.provider.TestThreadProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import org.junit.Rule

abstract class BaseTest {
    @Suppress("LeakingThis")
    @Rule
    @JvmField
    val injectMocks = InjectMocksRule.create(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val testThreadProvider: ThreadProvider = TestThreadProvider()
}
