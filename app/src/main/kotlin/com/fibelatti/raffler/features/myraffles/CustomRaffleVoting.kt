package com.fibelatti.raffler.features.myraffles

data class CustomRaffleVoting(
    val customRaffleId: Long,
    val description: String,
    val pin: Int,
    val totalVotes: Int,
    val votes: Map<String, Int>
)
