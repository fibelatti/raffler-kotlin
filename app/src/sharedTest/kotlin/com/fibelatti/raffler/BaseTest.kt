package com.fibelatti.raffler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.TestCoroutineLauncher
import org.junit.Rule

abstract class BaseTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val testCoroutineLauncher: CoroutineLauncher = TestCoroutineLauncher()
}
