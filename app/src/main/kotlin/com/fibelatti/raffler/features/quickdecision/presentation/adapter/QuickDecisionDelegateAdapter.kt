package com.fibelatti.raffler.features.quickdecision.presentation.adapter

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.core.android.base.BaseDelegateAdapter
import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionModel
import kotlinx.android.synthetic.main.list_item_quick_decision.view.*
import javax.inject.Inject

class QuickDecisionDelegateAdapter @Inject constructor() : BaseDelegateAdapter {
    var colorList: List<Int> = ArrayList()
    var clickListener: (View, QuickDecisionModel, Int) -> Unit = { _, _, _ -> }

    override fun getLayoutRes(): Int = R.layout.list_item_quick_decision

    override fun bindView(): View.(
        item: BaseViewType,
        viewHolder: RecyclerView.ViewHolder
    ) -> Unit = { item, viewHolder ->
        (item as? QuickDecisionModel)?.run {
            ViewCompat.setTransitionName(layoutQuickDecisionRoot, item.id)

            val color = colorList[viewHolder.layoutPosition % colorList.size]

            layoutQuickDecisionRoot.setShapeBackgroundColor(color)
            textViewQuickDecisionName.text = description
            setOnClickListener { clickListener(layoutQuickDecisionRoot, item, color) }
        }
    }
}
