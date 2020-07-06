package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.core.android.base.BaseDelegateAdapter
import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.raffler.R
import kotlinx.android.synthetic.main.list_item_custom_raffle_voting_result_title.view.*
import javax.inject.Inject

class CustomRaffleVotingResultsTitleDelegateAdapter @Inject constructor() : BaseDelegateAdapter {

    override fun getLayoutRes(): Int = R.layout.list_item_custom_raffle_voting_result_title

    override fun bindView(): View.(
        item: BaseViewType,
        viewHolder: RecyclerView.ViewHolder
    ) -> Unit = { item, _ ->
        textViewTitle.text = (item as? CustomRaffleVotingResultTitleModel)?.title
    }
}
