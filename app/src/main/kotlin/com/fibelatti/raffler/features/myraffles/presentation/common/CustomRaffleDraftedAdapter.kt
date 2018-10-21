package com.fibelatti.raffler.features.myraffles.presentation.common

import android.view.View
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_custom_raffle_drafted_item.view.*
import javax.inject.Inject

class CustomRaffleDraftedAdapter @Inject constructor() : BaseAdapter<CustomRaffleDraftedModel>() {

    var colorList: List<Int> = ArrayList()

    override fun getLayoutRes(): Int = R.layout.list_item_custom_raffle_drafted_item

    override fun View.bindView(item: CustomRaffleDraftedModel, viewHolder: ViewHolder) {
        layoutRootModeItem.setShapeBackgroundColor(colorList[viewHolder.layoutPosition % colorList.size])
        textViewTitle.text = item.title
        textViewItems.text = item.description
    }
}
