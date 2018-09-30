package com.fibelatti.raffler.randomize

import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.core.extension.shouldBeRight
import com.fibelatti.raffler.core.extension.shouldContain
import com.fibelatti.raffler.core.extension.sizeShouldBe
import com.fibelatti.raffler.features.randomize.Randomize
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class RandomizeTest : BaseTest() {

    private val randomize = Randomize()

    @Test
    fun `WHEN raffleQuantity is greater than totalQuantity THEN list contains all numbers`() {
        // WHEN
        val result = runBlocking { randomize(Randomize.Params(totalQuantity = 3, raffleQuantity = 10)) }

        // THEN
        result.shouldBeRight()
        result.rightOrNull()?.let { list ->
            list sizeShouldBe 3
            list shouldContain 0
            list shouldContain 1
            list shouldContain 2
        }
    }

    @Test
    fun `WHEN raffleQuantity is less than totalQuantity THEN all numbers should be less than totalQuantity`() {
        // WHEN
        val param = ArrayList<Int>().apply { (0 until 10).forEach { add(it) } }
        val result = runBlocking { randomize(Randomize.Params(totalQuantity = 10, raffleQuantity = 5)) }

        // THEN
        result.shouldBeRight()
        result.rightOrNull()?.let { list ->
            list sizeShouldBe 5
            param shouldContain list
        }
    }
}
