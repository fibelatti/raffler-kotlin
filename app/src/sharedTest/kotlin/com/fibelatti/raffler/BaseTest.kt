package com.fibelatti.raffler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.TestCoroutineLauncher
import org.junit.Rule

abstract class BaseTest {

    /* ViewModel tests should use jUnit 4 as rules are not supported in jUnit 5 */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val testCoroutineLauncher: CoroutineLauncher = TestCoroutineLauncher()
}
