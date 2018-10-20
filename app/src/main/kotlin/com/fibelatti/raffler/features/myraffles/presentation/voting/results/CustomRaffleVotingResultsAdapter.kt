package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import com.fibelatti.raffler.core.platform.base.BaseAdapterWithDelegates
import javax.inject.Inject

class CustomRaffleVotingResultsAdapter @Inject constructor(
    private val customRaffleVotingResultsTitleDelegateAdapter: CustomRaffleVotingResultsTitleDelegateAdapter,
    private val customRaffleVotingResultsDelegateAdapter: CustomRaffleVotingResultsDelegateAdapter
) : BaseAdapterWithDelegates() {
    init {
        delegateAdapters.apply {
            put(CustomRaffleVotingResultTitleModel.VIEW_TYPE, customRaffleVotingResultsTitleDelegateAdapter)
            put(CustomRaffleVotingResultModel.VIEW_TYPE, customRaffleVotingResultsDelegateAdapter)
        }
    }
}
