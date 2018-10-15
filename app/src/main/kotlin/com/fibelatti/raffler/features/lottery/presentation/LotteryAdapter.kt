package com.fibelatti.raffler.features.lottery.presentation

import android.view.View
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_lottery.view.*
import javax.inject.Inject

class LotteryAdapter @Inject constructor() : BaseAdapter<LotteryNumberModel>() {

    override fun getLayoutRes(): Int = R.layout.list_item_lottery

    override fun View.bindView(item: LotteryNumberModel, viewHolder: ViewHolder) {
        textViewLotteryItem.text = item.value
    }
}
