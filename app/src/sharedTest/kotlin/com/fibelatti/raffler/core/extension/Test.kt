@file:Suppress("UNCHECKED_CAST")

package com.fibelatti.raffler.core.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import kotlin.reflect.KClass

/***
 * Mockito.any() returns null and that can be an issue when testing Kotlin code.
 * This function addresses that issue and enables the usage of this matcher.
 */
fun <T> safeAny(): T = Mockito.any<T>() ?: null as T

// region Coroutines
/**
 * This can be used when calling suspend functions in mocks, although since updating to Kotlin
 * 1.3 it is not working properly with argument matchers.
 * To test those scenarios make your Test Class inherit from [com.fibelatti.raffler.BaseTest]
 * and call start { ... } placing your test body inside. InvalidUseOfMatchersException is still
 * thrown but the tests will work.
 *
 * Example:
 * ```
 * @Test
 * fun myTest() {
 *     val mockedDependency: Dependency = mock()
 *
 *     val classToBeTested = ClassToBeTested(mockedDependency)
 *
 *     start {
 *         // GIVEN
 *         given(myMockedDependency.mySuspendFun())
 *             .willReturn(myMockedResponse)
 *
 *         // WHEN
 *         classToBeTested.methodToBeTested()
 *
 *         // THEN
 *         // assertions
 *     }
 * }
 *
 * ```
 */
fun <T> givenSuspend(methodCall: suspend () -> T): BDDMockito.BDDMyOngoingStubbing<T> =
    given(runBlocking { methodCall() })

fun <T> callSuspend(methodCall: suspend () -> T): T = runBlocking { methodCall() }
// endregion

// region Assertions
fun throwAssertionError() {
    throw AssertionFailedError("Object should not be null")
}

infix fun Any.shouldBe(otherValue: Any) {
    assertEquals(otherValue, this)
}

infix fun List<Any>.sizeShouldBe(value: Int) {
    assertTrue(
        "Expected: $value - Actual: $size",
        size == value
    )
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

fun <T> LiveData<T>.shouldNeverReceiveValues() {
    val observer = spy(Observer<T> { })
    observeForever(observer)
    verify(observer, never()).onChanged(any())
    removeObserver(observer)
}
// endregion
