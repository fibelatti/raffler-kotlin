package com.fibelatti.raffler.features.myraffles.presentation.common

import android.view.View
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_custom_raffle_selector.view.*
import javax.inject.Inject

class CustomRaffleSelectorAdapter @Inject constructor() : BaseAdapter<CustomRaffleModel>() {

    var clickListener: (customRaffle: CustomRaffleModel) -> Unit = { }

    override fun getLayoutRes(): Int = R.layout.list_item_custom_raffle_selector

    override fun View.bindView(item: CustomRaffleModel, viewHolder: ViewHolder) {
        textViewCustomRaffleDescription.text = item.description
        setOnClickListener { clickListener(item) }
    }
}
