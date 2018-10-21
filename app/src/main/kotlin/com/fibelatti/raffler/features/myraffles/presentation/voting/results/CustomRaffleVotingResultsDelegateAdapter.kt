package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.ProgressBarAnimation
import com.fibelatti.raffler.core.platform.base.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.base.BaseViewType
import kotlinx.android.synthetic.main.list_item_custom_raffle_voting_result.view.*
import java.text.MessageFormat
import javax.inject.Inject

private const val ANIM_DURATION = 1000L
private const val PROGRESS_MULTIPLIER = 100

class CustomRaffleVotingResultsDelegateAdapter @Inject constructor() : BaseDelegateAdapter {

    override fun getLayoutRes(): Int = R.layout.list_item_custom_raffle_voting_result

    override fun bindView(itemView: View, item: BaseViewType, viewHolder: RecyclerView.ViewHolder) {
        with(itemView) {
            (item as? CustomRaffleVotingResultModel)?.run {
                textViewDescription.text = item.description
                textViewNumberOfVotes?.text = MessageFormat.format(
                    resources.getText(R.string.custom_raffle_voting_results_item_votes).toString(),
                    item.numberOfVotes,
                    item.percentOfTotalVotes
                )

                val anim = ProgressBarAnimation(
                    progressBarPercent,
                    from = 0f,
                    to = item.percentOfTotalVotes * PROGRESS_MULTIPLIER
                ).apply { duration = ANIM_DURATION }

                progressBarPercent.startAnimation(anim)
            }
        }
    }
}
