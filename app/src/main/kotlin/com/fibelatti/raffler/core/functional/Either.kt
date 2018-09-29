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

inline fun <R> runCatching(block: () -> R): Either<Throwable, R> {
    return try {
        Either.right(block())
    } catch (exception: Throwable) {
        Either.left(exception)
    }
}

inline fun <T, R> T.runCatching(block: T.() -> R): Either<Throwable, R> {
    return try {
        Either.right(block())
    } catch (exception: Throwable) {
        Either.left(exception)
    }
}

fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> Either.left(a)
        is Either.Right -> fn(b)
    }

fun <T, R> Either<Throwable, R>.flatMapCatching(fn: (R) -> T): Either<Throwable, T> =
    when (this) {
        is Either.Left -> Either.left(a)
        is Either.Right -> runCatching { fn(b) }
    }

fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> = this.flatMap(fn.c(::right))
