package com.fibelatti.raffler.features.myraffles.presentation.voting

data class CustomRaffleVotingResultModel(
    val description: String,
    val numberOfVotes: Int,
    val percentOfTotalVotes: Float,
    val additionalInfo: String = ""
)
