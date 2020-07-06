package com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle

import android.view.View
import com.fibelatti.core.android.base.BaseAdapter
import com.fibelatti.raffler.R
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import kotlinx.android.synthetic.main.list_item_create_custom_raffle_item.view.*
import javax.inject.Inject

class CreateCustomRaffleAdapter @Inject constructor() : BaseAdapter<CustomRaffleItemModel>() {

    override fun getLayoutRes(): Int = R.layout.list_item_create_custom_raffle_item

    override fun View.bindView(item: CustomRaffleItemModel, viewHolder: ViewHolder) {
        textViewCreateCustomRaffleItemDescription.text = item.description
    }
}
