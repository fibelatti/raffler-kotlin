package com.fibelatti.raffler.core.platform

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class ItemOffsetDecoration(private val itemOffset: Int) : RecyclerView.ItemDecoration() {
    constructor(context: Context, @DimenRes itemOffsetId: Int) : this(context.resources.getDimensionPixelSize(itemOffsetId))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        (view.layoutParams as? GridLayoutManager.LayoutParams)?.apply {
            if (viewLayoutPosition == RecyclerView.NO_POSITION) {
                outRect.set(0, 0, 0, 0)
                return
            }

            // add edge margin only if item edge is not the grid edge
            outRect.apply {
                // is left grid edge?
                left = if (spanIndex == 0) 0 else itemOffset
                // is top grid edge?
                top = if (spanIndex == viewLayoutPosition) 0 else itemOffset
                right = 0
                bottom = 0
            }
        }
    }
}
