package com.fibelatti.raffler.core.extension

import com.fibelatti.raffler.BaseTest
import org.junit.Test

class TypeTest : BaseTest() {
    // region Boolean
    @Test
    fun `WHEN boolean is null THEN orFalse returns false`() {
        // GIVEN
        val boolean: Boolean? = null

        // THEN
        boolean.orFalse() shouldBe false
    }

    @Test
    fun `WHEN boolean is null THEN orTrue returns true`() {
        // GIVEN
        val boolean: Boolean? = null

        // THEN
        boolean.orTrue() shouldBe true
    }
    // endregion

    // region String
    @Test
    fun `WHEN string is an int THEN isInt returns true`() {
        "10".isInt() shouldBe true
    }

    @Test
    fun `WHEN string is not an int THEN isInt returns false`() {
        "abc".isInt() shouldBe false
    }
    // endregion

    // region List
    @Test
    fun `WHEN batches is called THEN subLists are returned`() {
        // GIVEN
        val originalList = List(10) { it }

        // WHEN
        val batchesOfFive = originalList.batchesOf(5)
        val batchesOf3 = originalList.batchesOf(3)

        // THEN
        batchesOfFive sizeShouldBe 2
        batchesOfFive[0] shouldContain listOf(1, 2, 3, 4, 5)
        batchesOfFive[1] shouldContain listOf(6, 7, 8, 9, 10)

        batchesOf3 sizeShouldBe 4
        batchesOf3[0] shouldContain listOf(1, 2, 3)
        batchesOf3[1] shouldContain listOf(4, 5, 6)
        batchesOf3[2] shouldContain listOf(7, 8, 9)
        batchesOf3[3] shouldContain listOf(10)
    }
    // endregion
}
