package com.fibelatti.raffler.core.extension

import com.fibelatti.raffler.BaseTest
import org.junit.Test

class TypeTest : BaseTest() {
    @Test
    fun `WHEN boolean is null AND orFalse is called THEN false is returned`() {
        // GIVEN
        val boolean: Boolean? = null

        // THEN
        boolean.orFalse() shouldBe false
    }

    @Test
    fun `WHEN boolean is null AND orTrue is called THEN true is returned`() {
        // GIVEN
        val boolean: Boolean? = null

        // THEN
        boolean.orTrue() shouldBe true
    }
}
