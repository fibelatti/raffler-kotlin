package com.fibelatti.raffler.core.functional

import com.fibelatti.raffler.core.extension.mock
import com.fibelatti.raffler.core.extension.shouldBe
import com.fibelatti.raffler.core.extension.throwAssertionError
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyZeroInteractions
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class EitherTest {

    private val mockValue = true
    private val mockDefaultValue = false
    private val mockError = mock<Exception>()

    private val right: Either<Throwable, Boolean> = Either.right(mockValue)
    private val left: Either<Throwable, Boolean> = Either.left(mockError)

    private val mockFnR = spy({ _: Boolean -> Unit })
    private val mockFnL = spy({ _: Throwable -> Unit })

    private val success = Success(mockValue)
    private val failure = Failure(mockError)

    private val mockOnSuccess = spy({ value: Boolean -> value })
    private val mockOnFailure = spy({ _: Throwable -> mockDefaultValue })

    @Nested
    inner class Left {
        @Test
        fun `WHEN Either left is called THEN Left is returned`() {
            Either.left(mockError) shouldBe Either.Left(mockError)
        }

        @Test
        fun `GIVEN Either is Left AND either is called THEN fnL is invoked`() {
            // WHEN
            left.either(mockFnL, mockFnR)

            // THEN
            verify(mockFnL).invoke(mockError)
            verifyZeroInteractions(mockFnR)
        }

        @Test
        fun `GIVEN Either is Left AND leftOrNull is called THEN left value is returned`() {
            left.leftOrNull() shouldBe mockError
        }

        @Test
        fun `GIVEN Either is Left AND rightOrNull is called THEN null is returned`() {
            left.rightOrNull() shouldBe null
        }
    }

    @Nested
    inner class Right {
        @Test
        fun `WHEN Either right is called THEN Right is returned`() {
            Either.right(mockValue) shouldBe Either.Right(mockValue)
        }

        @Test
        fun `GIVEN Either is Right AND either is called THEN fnR is invoked`() {
            // WHEN
            right.either(mockFnL, mockFnR)

            // THEN
            verify(mockFnR).invoke(mockValue)
            verifyZeroInteractions(mockFnL)
        }

        @Test
        fun `GIVEN Either is Right AND leftOrNull is called THEN null is returned`() {
            right.leftOrNull() shouldBe null
        }

        @Test
        fun `GIVEN Either is Right AND rightOrNull is called THEN right value is returned`() {
            right.rightOrNull() shouldBe mockValue
        }
    }

    @Nested
    inner class ResultSuccess {
        @Test
        fun `GIVEN Result is Success WHEN value is called THEN value is returned`() {
            success.value shouldBe mockValue
        }

        @Test
        fun `GIVEN Result is Success WHEN isSuccess is called THEN true is returned`() {
            success.isSuccess shouldBe true
        }

        @Test
        fun `GIVEN Result is Success WHEN isFailure is called THEN false is returned`() {
            success.isFailure shouldBe false
        }

        @Test
        fun `GIVEN Result is Success WHEN getOrNull is called THEN value is returned`() {
            success.getOrNull() shouldBe mockValue
        }

        @Test
        fun `GIVEN Result is Success WHEN exceptionOrNull is called THEN null is returned`() {
            success.exceptionOrNull() shouldBe null
        }

        @Test
        fun `GIVEN Result is Success WHEN throwOnFailure is called THEN nothing happens`() {
            try {
                success.throwOnFailure()
            } catch (exception: Exception) {
                throwAssertionError()
            }
        }

        @Test
        fun `GIVEN Result is Success WHEN getOrThrow is called THEN value is returned`() {
            try {
                success.getOrThrow() shouldBe mockValue
            } catch (exception: Exception) {
                throwAssertionError()
            }
        }

        @Test
        fun `GIVEN Result is Success WHEN getOrElse is called THEN value is returned`() {
            success.getOrElse(mockFnL) shouldBe mockValue
            verifyZeroInteractions(mockFnL)
        }

        @Test
        fun `GIVEN Result is Success WHEN getOrDefault is called THEN value is returned`() {
            success.getOrDefault(mockDefaultValue) shouldBe mockValue
        }

        @Test
        fun `GIVEN Result is Success WHEN fold is called THEN onSuccess is called`() {
            // WHEN
            success.fold(mockOnSuccess, mockOnFailure)

            // THEN
            verify(mockOnSuccess).invoke(mockValue)
            verifyZeroInteractions(mockOnFailure)
        }

        @Test
        fun `GIVEN Result is Success WHEN onFailure is called THEN nothing happens`() {
            // WHEN
            success.onFailure(mockFnL)

            // THEN
            verifyZeroInteractions(mockFnL)
        }

        @Test
        fun `GIVEN Result is Success WHEN onSuccess is called THEN function is invoked`() {
            // WHEN
            success.onSuccess(mockFnR)

            // THEN
            verify(mockFnR).invoke(mockValue)
        }

        @Test
        fun `GIVEN Result is Success WHEN mapCatching is called THEN function is invoked`() {
            // WHEN
            success.mapCatching(mockFnR)

            // THEN
            verify(mockFnR).invoke(mockValue)
        }
    }

    @Nested
    inner class ResultFailure {
        @Test
        fun `GIVEN Result is Failure WHEN error is called THEN value is returned`() {
            failure.error shouldBe mockError
        }

        @Test
        fun `GIVEN Result is Failure WHEN isSuccess is called THEN false is returned`() {
            failure.isSuccess shouldBe false
        }

        @Test
        fun `GIVEN Result is Failure WHEN isFailure is called THEN true is returned`() {
            failure.isFailure shouldBe true
        }

        @Test
        fun `GIVEN Result is Failure WHEN getOrNull is called THEN null is returned`() {
            failure.getOrNull() shouldBe null
        }

        @Test
        fun `GIVEN Result is Failure WHEN exceptionOrNull is called THEN error is returned`() {
            failure.exceptionOrNull() shouldBe mockError
        }

        @Test
        fun `GIVEN Result is Failure WHEN throwOnFailure is called THEN error is thrown`() {
            assertThrows<Exception> { failure.throwOnFailure() }
        }

        @Test
        fun `GIVEN Result is Failure WHEN getOrThrow is called THEN error is thrown`() {
            assertThrows<Exception> { failure.getOrThrow() }
        }

        @Test
        fun `GIVEN Result is Failure WHEN getOrElse is called THEN function is invoked and its result returned`() {
            failure.getOrElse(mockOnFailure) shouldBe mockDefaultValue
        }

        @Test
        fun `GIVEN Result is Failure WHEN getOrDefault is called THEN default value is returned`() {
            failure.getOrDefault(mockDefaultValue) shouldBe mockDefaultValue
        }

        @Test
        fun `GIVEN Result is Failure WHEN fold is called THEN onFailure is called`() {
            // WHEN
            failure.fold(mockOnSuccess, mockOnFailure)

            // THEN
            verifyZeroInteractions(mockOnSuccess)
            verify(mockOnFailure).invoke(mockError)
        }

        @Test
        fun `GIVEN Result is Failure WHEN onFailure is called THEN function is invoked`() {
            // WHEN
            failure.onFailure(mockFnL)

            // THEN
            verify(mockFnL).invoke(mockError)
        }

        @Test
        fun `GIVEN Result is Failure WHEN onSuccess is called THEN nothing happens`() {
            // WHEN
            failure.onSuccess(mockFnR)

            // THEN
            verifyZeroInteractions(mockFnR)
        }

        @Test
        fun `GIVEN Result is Failure WHEN mapCatching is called THEN error is returned`() {
            failure.mapCatching(mockOnSuccess) shouldBe failure
        }
    }

    @Nested
    inner class Result {
        @Test
        fun `GIVEN catching is called WHEN block throws an exception THEN Failure is returned`() {
            // GIVEN
            val function = spy({ true })
            given(function.invoke())
                .willAnswer { throw mockError }

            // THEN
            catching(function) shouldBe Failure(mockError)
        }

        @Test
        fun `WHEN catching is called THEN Success is returned`() {
            // GIVEN
            val function = spy({ true })

            // THEN
            catching(function) shouldBe Success(true)
        }
    }
}
