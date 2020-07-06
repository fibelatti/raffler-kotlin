package com.fibelatti.raffler.core.platform

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation(
    private val progressBar: ProgressBar,
    private val from: Float,
    private val to: Float
) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        progressBar.progress = (from + (to - from) * interpolatedTime).toInt()
    }
}
