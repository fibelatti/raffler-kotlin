package com.fibelatti.raffler.features.quickdecision.presentation.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.base.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionModel
import kotlinx.android.synthetic.main.list_item_quick_decision.view.*
import javax.inject.Inject

class QuickDecisionDelegateAdapter @Inject constructor() : BaseDelegateAdapter {
    var colorList: List<Int> = ArrayList()
    var clickListener: (View, QuickDecisionModel, Int) -> Unit = { _, _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = DataViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: BaseViewType) {
        (holder as? DataViewHolder)?.bind(item as? QuickDecisionModel)
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_quick_decision)
    ) {
        fun bind(item: QuickDecisionModel?) = with(itemView) {
            item?.run {
                ViewCompat.setTransitionName(layoutQuickDecisionRoot, item.id)

                val color = colorList[layoutPosition % colorList.size]

                layoutQuickDecisionRoot.setShapeBackgroundColor(color)
                textViewQuickDecisionName.text = description
                setOnClickListener { clickListener(layoutQuickDecisionRoot, item, color) }
            }
        }
    }
}
