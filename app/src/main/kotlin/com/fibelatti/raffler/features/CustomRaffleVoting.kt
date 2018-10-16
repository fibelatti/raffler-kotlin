package com.fibelatti.raffler.features

data class CustomRaffleVoting(
    val customRaffleId: Long,
    val description: String,
    val pin: Int,
    val totalVotes: Int,
    val votes: Map<String, Int>
)
