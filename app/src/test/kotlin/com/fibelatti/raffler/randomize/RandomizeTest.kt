package com.fibelatti.raffler.randomize

import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.core.extension.callSuspend
import com.fibelatti.raffler.core.extension.shouldBeAnInstanceOf
import com.fibelatti.raffler.core.extension.shouldContain
import com.fibelatti.raffler.core.extension.sizeShouldBe
import com.fibelatti.raffler.core.extension.throwAssertionError
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.features.randomize.Randomize
import org.junit.Test

class RandomizeTest : BaseTest() {

    private val randomize = Randomize()

    @Test
    fun `WHEN raffleQuantity is greater than totalQuantity THEN list contains all numbers`() {
        // WHEN
        val result = callSuspend { randomize(Randomize.Params(totalQuantity = 3, raffleQuantity = 10)) }

        // THEN
        result shouldBeAnInstanceOf Success::class
        result.rightOrNull()?.let { list ->
            list sizeShouldBe 3
            list shouldContain 0
            list shouldContain 1
            list shouldContain 2
        } ?: throwAssertionError()
    }

    @Test
    fun `WHEN raffleQuantity is less than totalQuantity THEN all numbers should be less than totalQuantity`() {
        // WHEN
        val param = ArrayList<Int>().apply { (0 until 10).forEach { add(it) } }
        val result = callSuspend { randomize(Randomize.Params(totalQuantity = 10, raffleQuantity = 5)) }

        // THEN
        result shouldBeAnInstanceOf Success::class
        result.rightOrNull()?.let { list ->
            list sizeShouldBe 5
            param shouldContain list
        } ?: throwAssertionError()
    }
}
