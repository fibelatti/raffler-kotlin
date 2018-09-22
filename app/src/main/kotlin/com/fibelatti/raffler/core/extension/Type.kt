package com.fibelatti.raffler.core.extension

fun Boolean?.orFalse(): Boolean = this ?: false

fun Boolean?.orTrue(): Boolean = this ?: true

fun Int?.orZero(): Int = this ?: 0

fun Long?.orZero(): Long = this ?: 0

inline fun String?.ifNotNullOrEmpty(block: (String) -> Unit) {
    if (this != null && isNotEmpty()) block(this)
}

fun String.Companion.empty(): String = ""

fun String.remove(value: String): String = replace(value, "")

fun String.remove(regex: Regex): String = replace(regex, "")

fun String.removeFirst(value: String): String = replaceFirst(value, "")

fun String.removeFirst(regex: Regex): String = replaceFirst(regex, "")

fun CharSequence.remove(regex: Regex): String = replace(regex, "")

fun CharSequence.removeFirst(regex: Regex): String = replaceFirst(regex, "")

/***
 * Map.getOrDefault was added in Java 8. This ExFun mimics its implementation.
 */
fun <K, V> Map<K, V>.getOrDefaultValue(key: K, defaultValue: V): V =
    get(key)?.let { it } ?: defaultValue
