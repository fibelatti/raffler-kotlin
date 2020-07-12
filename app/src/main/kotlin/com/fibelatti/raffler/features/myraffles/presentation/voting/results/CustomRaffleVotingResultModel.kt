package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import com.fibelatti.core.android.base.BaseViewType

data class CustomRaffleVotingResultModel(
    val description: String,
    val numberOfVotes: Int,
    val percentOfTotalVotes: Float
) : BaseViewType {

    companion object {
        @JvmStatic
        val VIEW_TYPE = CustomRaffleVotingResultModel::class.hashCode()
    }

    override fun getViewType() = VIEW_TYPE
}
