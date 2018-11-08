@file:Suppress("UNCHECKED_CAST")

package com.fibelatti.raffler.core.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.fibelatti.raffler.core.platform.Event
import junit.framework.AssertionFailedError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.verification.VerificationMode

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

/***
 * Mockito.any() returns null and that can be an issue when testing Kotlin code.
 * This function addresses that issue and enables the usage of this matcher.
 */
fun <T> safeAny(): T = Mockito.any<T>() ?: null as T

// region Coroutines
fun <T> givenSuspend(methodCall: suspend () -> T): BDDMockito.BDDMyOngoingStubbing<T> =
    given(runBlocking { methodCall() })

fun <T> callSuspend(methodCall: suspend () -> T): T = runBlocking { methodCall() }

fun <T> verifySuspend(mock: T, methodCall: suspend T.() -> Any) {
    runBlocking { verify(mock).run { methodCall() } }
}

fun <T> verifySuspend(mock: T, verificationMode: VerificationMode, methodCall: suspend T.() -> Any) {
    runBlocking { verify(mock, verificationMode).run { methodCall() } }
}
// endregion

// region Assertions
fun throwAssertionError() {
    throw AssertionFailedError("The expected condition was not fulfilled.")
}

infix fun Any?.shouldBe(otherValue: Any?) {
    assertEquals(otherValue, this)
}

infix fun List<Any>.sizeShouldBe(value: Int) {
    assertTrue(
        "Expected size: $value - Actual size: $size",
        size == value
    )
}

fun List<Any>.shouldBeEmpty() {
    assertTrue(
        "Expected size: 0 - Actual size: $size",
        this.isEmpty()
    )
}

inline fun <reified T : Any> Any.shouldBeAnInstanceOf() {
    assertTrue(
        "Expected: ${T::class.java} - Actual: ${this::class.java}",
        this::class == T::class
    )
}

infix fun <T> List<T>.shouldContain(value: T) {
    assertTrue(contains(value))
}

infix fun <T> List<T>.shouldContain(subList: List<T>) {
    assertTrue(
        "Expected: $subList - Actual: $this",
        containsAll(subList)
    )
}

infix fun <T> LiveData<T>.shouldReceive(expectedValue: T) {
    var value: T? = null
    val observer = Observer<T> { value = it }
    observeForever(observer)
    assertEquals(expectedValue, value)
    removeObserver(observer)
}

infix fun <T> LiveData<Event<T>>.shouldReceiveEventWithValue(expectedValue: T) {
    shouldReceive(Event(expectedValue))
}

fun <T> LiveData<T>.shouldNeverReceiveValues() {
    val observer = spy(Observer<T> { })
    observeForever(observer)
    verify(observer, never()).onChanged(any())
    removeObserver(observer)
}
// endregion
