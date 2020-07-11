package com.fibelatti.raffler.core.platform.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.core.extension.orZero
import com.fibelatti.raffler.R

private const val ROUNDED_CORNER_OFFSET = 20

abstract class RecyclerViewSwipeToEditCallback(
    context: Context
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_edit)
    private val intrinsicWidth = icon?.intrinsicWidth.orZero()
    private val intrinsicHeight = icon?.intrinsicHeight.orZero()

    private val swipeBackground = ContextCompat.getDrawable(context, R.drawable.background_swipe_to_edit)
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    @Suppress("MagicNumber")
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        with(viewHolder.itemView) {
            val itemHeight = bottom - top
            val isCanceled = dX <= 200F && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(canvas, right + dX, top.toFloat(), right.toFloat(), bottom.toFloat())
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return@with
            }

            // Draw the background
            swipeBackground?.apply {
                setBounds(left, top, left + ROUNDED_CORNER_OFFSET + dX.toInt(), bottom)
                draw(canvas)
            }

            // Calculate position of the icon
            val iconTop = top + (itemHeight - intrinsicHeight) / 2
            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconLeft = left + iconMargin
            val iconRight = left + iconMargin + intrinsicWidth
            val iconBottom = iconTop + intrinsicHeight

            // Draw the icon
            icon?.run {
                setBounds(iconLeft, iconTop, iconRight, iconBottom)
                draw(canvas)
            }

            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
