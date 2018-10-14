package com.fibelatti.raffler.features.myraffles.presentation.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.base.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.list_item_custom_raffle.view.*
import javax.inject.Inject

class CustomRaffleDelegateAdapter @Inject constructor() : BaseDelegateAdapter {
    var colorList: List<Int> = ArrayList()
    var clickListener: (Long) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = DataViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: BaseViewType) {
        (holder as? DataViewHolder)?.bind(item as? CustomRaffleModel)
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_custom_raffle)
    ) {
        fun bind(item: CustomRaffleModel?) = with(itemView) {
            item?.run {
                val color = colorList[layoutPosition % colorList.size]

                layoutCustomRaffleRoot.setShapeBackgroundColor(color)
                textViewCustomRaffleDescription.text = description
                textViewCustomRaffleItems.text = context.getString(R.string.my_raffles_items_quantity, items.size)
                setOnClickListener { clickListener(item.id) }
            }
        }
    }
}
