package com.fibelatti.raffler.features.myraffles.presentation.voting.results

data class CustomRaffleVotingResultModel(
    val description: String,
    val numberOfVotes: Int,
    val percentOfTotalVotes: Float,
    val additionalInfo: String = ""
)
