package com.fibelatti.raffler.features.myraffles.presentation.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.base.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.list_item_custom_raffle.view.*
import javax.inject.Inject

class CustomRaffleDelegateAdapter @Inject constructor() : BaseDelegateAdapter {
    var colorList: List<Int> = ArrayList()
    var clickListener: (Long) -> Unit = {}

    override fun getLayoutRes(): Int = R.layout.list_item_custom_raffle

    override fun bindView(itemView: View, item: BaseViewType, viewHolder: RecyclerView.ViewHolder) {
        with(itemView) {
            (item as? CustomRaffleModel)?.run {
                val color = colorList[viewHolder.layoutPosition % colorList.size]

                layoutCustomRaffleRoot.setShapeBackgroundColor(color)
                textViewCustomRaffleDescription.text = description
                textViewCustomRaffleItems.text = context.getString(R.string.my_raffles_items_quantity, items.size)
                setOnClickListener { clickListener(item.id) }
            }
        }
    }
}
