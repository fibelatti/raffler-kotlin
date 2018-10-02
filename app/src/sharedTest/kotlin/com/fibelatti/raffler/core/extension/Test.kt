@file:Suppress("UNCHECKED_CAST")

package com.fibelatti.raffler.core.extension

import junit.framework.AssertionFailedError
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.reflect.KClass

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
fun throwAssertionError() {
    throw AssertionFailedError("Object should not be null")
}

infix fun Any.shouldBe(otherValue: Any) {
    assertEquals(otherValue, this)
}

infix fun List<Any>.sizeShouldBe(value: Int) {
    assertTrue(size == value)
}

infix fun <T : Any> Any.shouldBeAnInstanceOf(KClass: KClass<T>) {
    assertTrue(
        "Expected: ${KClass.java} - Actual: ${this::class.java}",
        this::class == KClass
    )
}

infix fun <T> List<T>.shouldContain(value: T) {
    assertTrue(contains(value))
}

infix fun <T> List<T>.shouldContain(subList: List<T>) {
    assertTrue(containsAll(subList))
}
// endregion
