@file:Suppress("UNCHECKED_CAST")

package com.fibelatti.raffler.core.extension

import org.mockito.Mockito
import org.mockito.Mockito.mock

/***
 * Mockito.any() returns null and that can be an issue when testing Kotlin code.
 * This function addresses that issue and enables the usage of this matcher.
 */
fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T

inline fun <reified T : Any> mock(): T = mock(T::class.java)
