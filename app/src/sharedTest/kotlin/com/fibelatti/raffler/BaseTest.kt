package com.fibelatti.raffler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach

abstract class BaseTest {

    @BeforeEach
    fun baseSetup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
}
