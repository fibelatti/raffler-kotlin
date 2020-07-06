package com.fibelatti.raffler.core.extension

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.ViewGroup

fun ViewGroup.setShapeBackgroundColor(color: Int) {
    when (val background = background) {
        is ShapeDrawable -> background.paint?.color = color
        is GradientDrawable -> background.setColor(color)
        is ColorDrawable -> background.color = color
    }
}
