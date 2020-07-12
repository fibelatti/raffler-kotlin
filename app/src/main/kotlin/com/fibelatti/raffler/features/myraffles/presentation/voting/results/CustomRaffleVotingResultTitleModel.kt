package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import com.fibelatti.core.android.base.BaseViewType

data class CustomRaffleVotingResultTitleModel(val title: String) : BaseViewType {

    companion object {
        @JvmStatic
        val VIEW_TYPE = CustomRaffleVotingResultTitleModel::class.hashCode()
    }

    override fun getViewType() = VIEW_TYPE
}
