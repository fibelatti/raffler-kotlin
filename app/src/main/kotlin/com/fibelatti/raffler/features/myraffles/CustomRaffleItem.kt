package com.fibelatti.raffler.features.myraffles

data class CustomRaffleItem(
    val id: Long,
    val customRaffleId: Long,
    val description: String,
    val included: Boolean
)
