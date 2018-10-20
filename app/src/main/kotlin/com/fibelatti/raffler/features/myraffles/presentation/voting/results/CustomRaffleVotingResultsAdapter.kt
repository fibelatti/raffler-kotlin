package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import android.view.View
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.platform.ProgressBarAnimation
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_custom_raffle_voting_result.view.*
import java.text.MessageFormat
import javax.inject.Inject

private const val ANIM_DURATION = 1000L

class CustomRaffleVotingResultsAdapter @Inject constructor() : BaseAdapter<CustomRaffleVotingResultModel>() {

    override fun getLayoutRes(): Int = R.layout.list_item_custom_raffle_voting_result

    override fun View.bindView(item: CustomRaffleVotingResultModel, viewHolder: ViewHolder) {
        if (item.additionalInfo.isBlank()) {
            textViewAdditionalInfo.gone()
            layoutDivider.gone()
        } else {
            textViewAdditionalInfo.text = item.additionalInfo
            textViewAdditionalInfo.visible()
            layoutDivider.visible()
        }

        textViewDescription.text = item.description
        textViewNumberOfVotes?.text = MessageFormat.format(
            resources.getText(R.string.custom_raffle_voting_results_item_votes).toString(),
            item.numberOfVotes,
            item.percentOfTotalVotes
        )

        val anim = ProgressBarAnimation(progressBarPercent, from = 0f, to = item.percentOfTotalVotes)
            .apply { duration = ANIM_DURATION }

        progressBarPercent.startAnimation(anim)
    }
}
