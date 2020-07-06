package com.fibelatti.raffler.core.platform.customview

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

private const val MIN_DISTANCE = 200

class ViewOnTouchListener : OnTouchListener {

    private var downX: Float = 0f
    private var downY: Float = 0f
    private var originalX: Float = 0f
    private var originalY: Float = 0f

    var onRightToLeftSwipe: (() -> Unit)? = null
    var onLeftToRightSwipe: (() -> Unit)? = null
    var onTopToBottomSwipe: (() -> Unit)? = null
    var onBottomToTopSwipe: (() -> Unit)? = null

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        view.performClick()
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> handleActionDown(view, event)
            MotionEvent.ACTION_MOVE -> handleActionMove(view, event)
            MotionEvent.ACTION_UP -> handleActionUp(view)
            else -> false
        }
    }

    private fun handleActionDown(view: View, event: MotionEvent): Boolean {
        originalX = view.x
        originalY = view.y

        downX = view.x - event.rawX
        downY = view.y - event.rawY

        return true
    }

    private fun handleActionMove(view: View, event: MotionEvent): Boolean {
        view.animate()
            .apply {
                when {
                    onRightToLeftSwipe != null && event.rawX + downX > 0 -> x(event.rawX + downX)
                    onLeftToRightSwipe != null && event.rawX + downX < 0 -> x(event.rawX + downX)
                }
            }
            .apply {
                when {
                    onBottomToTopSwipe != null && event.rawY + downY < 0 -> y(event.rawY + downY)
                    onTopToBottomSwipe != null && event.rawY + downY > 0 -> y(event.rawY + downY)
                }
            }
            .setDuration(0)
            .start()

        return true
    }

    private fun handleActionUp(view: View): Boolean {
        view.performClick()

        return when {
            onRightToLeftSwipe != null && abs(view.x) > MIN_DISTANCE && view.x < 0 -> {
                onRightToLeftSwipe?.invoke()
                true
            }
            onLeftToRightSwipe != null && abs(view.x) > MIN_DISTANCE && view.x > 0 -> {
                onLeftToRightSwipe?.invoke()
                true
            }
            onBottomToTopSwipe != null && abs(view.y) > MIN_DISTANCE && view.y < 0 -> {
                onBottomToTopSwipe?.invoke()
                true
            }
            onTopToBottomSwipe != null && abs(view.y) > MIN_DISTANCE && view.y > 0 -> {
                onTopToBottomSwipe?.invoke()
                true
            }
            else -> {
                view.animate()
                    .x(originalX)
                    .y(originalY)
                    .setDuration(0)
                    .start()
                false
            }
        }
    }
}
