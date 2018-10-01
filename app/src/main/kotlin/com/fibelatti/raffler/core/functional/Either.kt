package com.fibelatti.raffler.core.functional

import com.fibelatti.raffler.core.functional.Either.Companion.right
import com.fibelatti.raffler.core.functional.Either.Left
import com.fibelatti.raffler.core.functional.Either.Right

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun either(fnL: (L) -> Unit, fnR: (R) -> Unit): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }

    fun leftOrNull(): L? =
        when (this) {
            is Left -> a
            is Right -> null
        }

    fun rightOrNull(): R? =
        when (this) {
            is Left -> null
            is Right -> b
        }

    companion object {
        @JvmStatic
        fun <L> left(a: L) = Either.Left(a)

        @JvmStatic
        fun <R> right(b: R) = Either.Right(b)
    }

    /**
     * Represents the left side of [Either] class which by convention is a "Failure".
     */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /**
     * Represents the right side of [Either] class which by convention is a "Success".
     */
    data class Right<out R>(val b: R) : Either<Nothing, R>()
}

// region Result
typealias Result<T> = Either<Throwable, T>

typealias Success<T> = Either.Right<T>

typealias Failure<T> = Either.Left<T>

fun <R> Result<R>.getOrNull() = rightOrNull()

fun <R> Result<R>.exceptionOrNull() = leftOrNull()

fun <R> Result<R>.throwOnFailure() {
    if (this is Failure) throw a
}

fun <R> Result<R>.getOrThrow(): R {
    return when (this) {
        is Success -> b
        is Failure -> throw a
    }
}

fun <R> Result<R>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (this) {
        is Success -> b
        is Failure -> onFailure(a)
    }
}

fun <R> Result<R>.getOrDefault(defaultValue: R): R {
    return when (this) {
        is Success -> b
        is Failure -> defaultValue
    }
}

fun <R> Result<R>.fold(onSuccess: (R) -> R, onFailure: (Throwable) -> R): R {
    return when (this) {
        is Success -> onSuccess(b)
        is Failure -> onFailure(a)
    }
}

fun <T, R> Result<R>.flatMapCatching(fn: (R) -> T): Result<T> {
    return when (this) {
        is Success -> runCatching { fn(b) }
        is Failure -> Failure(a)
    }
}

inline fun <R> runCatching(block: () -> R): Result<R> {
    return try {
        Success(block())
    } catch (exception: Throwable) {
        Failure(exception)
    }
}

inline fun <T, R> T.runCatching(block: T.() -> R): Result<R> {
    return try {
        Success(block())
    } catch (exception: Throwable) {
        Failure(exception)
    }
}
// endregion

// region Transformation
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> = flatMap(fn.c(::right))

fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> Either.left(a)
        is Either.Right -> fn(b)
    }
// endregion
