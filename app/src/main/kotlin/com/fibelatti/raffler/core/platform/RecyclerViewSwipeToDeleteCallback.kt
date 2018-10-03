package com.fibelatti.raffler.core.platform

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orZero

private const val ROUNDED_CORNER_OFFSET = 20

abstract class RecyclerViewSwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val deleteIcon by lazy { ContextCompat.getDrawable(context, R.drawable.ic_delete) }
    private val intrinsicWidth by lazy { deleteIcon?.intrinsicWidth.orZero() }
    private val intrinsicHeight by lazy { deleteIcon?.intrinsicHeight.orZero() }

    private val swipeBackground by lazy { ContextCompat.getDrawable(context, R.drawable.background_swipe_to_delete) }
    private val clearPaint by lazy { Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) } }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

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
            val isCanceled = dX == 0f && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(canvas, right + dX, top.toFloat(), right.toFloat(), bottom.toFloat())
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            // Draw the red delete background
            swipeBackground?.apply {
                setBounds(right - ROUNDED_CORNER_OFFSET + dX.toInt(), top, right, bottom)
                draw(canvas)
            }

            // Calculate position of delete icon
            val deleteIconTop = top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            // Draw the delete icon
            deleteIcon?.run {
                setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                draw(canvas)
            }

            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
