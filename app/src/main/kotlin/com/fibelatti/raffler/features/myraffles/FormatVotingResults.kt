package com.fibelatti.raffler.features.myraffles

import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.core.functional.Result
import com.fibelatti.core.functional.UseCaseWithParams
import com.fibelatti.core.functional.catching
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingModel
import com.fibelatti.raffler.features.myraffles.presentation.voting.results.CustomRaffleVotingResultModel
import com.fibelatti.raffler.features.myraffles.presentation.voting.results.CustomRaffleVotingResultTitleModel
import javax.inject.Inject

class FormatVotingResults @Inject constructor(
    private val resourceProvider: ResourceProvider
) : UseCaseWithParams<List<BaseViewType>, CustomRaffleVotingModel>() {

    override suspend fun run(params: CustomRaffleVotingModel): Result<List<BaseViewType>> = catching {
        val resultList = mutableListOf<BaseViewType>()
        val titleOthers = CustomRaffleVotingResultTitleModel(
            resourceProvider.getString(R.string.custom_raffle_voting_results_others)
        )

        params.votes.toList()
            .sortedByDescending { (_, value) -> value }
            .groupBy { (_, value) -> value }.toList()
            .forEachIndexed { index, (_, tiedCandidates) ->
                when {
                    tiedCandidates.all { (_, numberOfVotes) -> numberOfVotes == 0 } -> {
                        CustomRaffleVotingResultTitleModel(
                            resourceProvider.getString(R.string.custom_raffle_voting_results_no_votes)
                        )
                    }
                    index == 0 -> CustomRaffleVotingResultTitleModel(
                        resourceProvider.getString(R.string.custom_raffle_voting_results_first_place)
                    )
                    index == 1 -> CustomRaffleVotingResultTitleModel(
                        resourceProvider.getString(R.string.custom_raffle_voting_results_second_place)
                    )
                    index == 2 -> CustomRaffleVotingResultTitleModel(
                        resourceProvider.getString(R.string.custom_raffle_voting_results_third_place)
                    )
                    else -> titleOthers.takeIf { !resultList.contains(it) }
                }?.let(resultList::add)

                tiedCandidates.forEach { (description, numberOfVotes) ->
                    resultList.add(CustomRaffleVotingResultModel(
                        description = description,
                        numberOfVotes = numberOfVotes,
                        percentOfTotalVotes = numberOfVotes / params.totalVotes.toFloat()
                    ))
                }
            }

        return@catching resultList
    }
}
