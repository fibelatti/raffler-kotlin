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
}
