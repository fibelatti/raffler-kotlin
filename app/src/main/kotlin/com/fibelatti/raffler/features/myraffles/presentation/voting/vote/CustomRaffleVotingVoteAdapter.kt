package com.fibelatti.raffler.features.myraffles.presentation.voting.vote

import android.view.View
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_create_custom_raffle_item.view.*
import javax.inject.Inject

class CustomRaffleVotingVoteAdapter @Inject constructor() : BaseAdapter<String>() {

    var clickListener: (item: String) -> Unit = { }

    override fun getLayoutRes(): Int = R.layout.list_item_create_custom_raffle_item

    override fun View.bindView(item: String, viewHolder: ViewHolder) {
        textViewCreateCustomRaffleItemDescription.text = item
        setOnClickListener { clickListener(item) }
    }
}
