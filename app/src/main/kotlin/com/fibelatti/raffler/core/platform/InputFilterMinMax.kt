package com.fibelatti.raffler.core.platform

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(
    private val min: Int,
    private val max: Int
) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {

        return when {
            source.isEmpty() -> source
            (dest.toString() + source).toIntOrNull() ?: 0 < min -> ""
            (dest.toString() + source).toIntOrNull() ?: 0 > max -> ""
            else -> source
        }
    }
}
