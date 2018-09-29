@file:Suppress("UNCHECKED_CAST")

package com.fibelatti.raffler.core.extension

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock

/***
 * Mockito.any() returns null and that can be an issue when testing Kotlin code.
 * This function addresses that issue and enables the usage of this matcher.
 */
fun <T> safeAny(): T = Mockito.any<T>() ?: uninitialized()

private fun <T> uninitialized(): T = null as T

inline fun <reified T : Any> mock(): T = mock(T::class.java)

fun <T> givenSuspend(methodCall: suspend () -> T): BDDMockito.BDDMyOngoingStubbing<T> {
    return given(runBlocking { methodCall() })
}

// region Assertions
infix fun Any.shouldBe(otherValue: Any) {
    assertEquals(otherValue, this)
}
// endregion
