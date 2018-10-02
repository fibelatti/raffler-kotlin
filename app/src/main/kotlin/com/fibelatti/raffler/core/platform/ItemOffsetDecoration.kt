package com.fibelatti.raffler.core.platform

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(private val itemOffset: Int) : RecyclerView.ItemDecoration() {
    constructor(context: Context, @DimenRes itemOffsetId: Int) : this(context.resources.getDimensionPixelSize(itemOffsetId))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutParams = view.layoutParams

        when (layoutParams) {
            is GridLayoutManager.LayoutParams -> {
                layoutParams.apply {
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
            is RecyclerView.LayoutParams -> {
                layoutParams.apply {
                    if (viewLayoutPosition == RecyclerView.NO_POSITION) {
                        outRect.set(0, 0, 0, 0)
                        return
                    }

                    // add edge margin only if item edge is not the grid edge
                    outRect.apply {
                        left = 0
                        // is top grid edge?
                        top = if (viewLayoutPosition == 0) 0 else itemOffset
                        right = 0
                        bottom = 0
                    }
                }
            }
        }
    }
}
