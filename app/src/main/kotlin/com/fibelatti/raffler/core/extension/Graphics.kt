package com.fibelatti.raffler.core.extension

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun getColorGradientForListSize(
    context: Context,
    @ColorRes startColorRes: Int,
    @ColorRes endColorRes: Int,
    listSize: Int
): List<Int> {
    val steps = if (listSize > 1) listSize - 1 else 1
    val startColor = ContextCompat.getColor(context, startColorRes)
    val endColor = ContextCompat.getColor(context, endColorRes)

    val r1 = Color.red(startColor)
    val g1 = Color.green(startColor)
    val b1 = Color.blue(startColor)

    val r2 = Color.red(endColor)
    val g2 = Color.green(endColor)
    val b2 = Color.blue(endColor)

    val redStep = (r2 - r1) / steps
    val greenStep = (g2 - g1) / steps
    val blueStep = (b2 - b1) / steps

    return (0..steps).map { Color.rgb(r1 + redStep * it, g1 + greenStep * it, b1 + blueStep * it) }
}
