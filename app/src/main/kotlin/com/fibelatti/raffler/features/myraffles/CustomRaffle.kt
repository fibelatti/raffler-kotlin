package com.fibelatti.raffler.features.myraffles

data class CustomRaffle(
    val id: Long,
    val description: String,
    val items: List<CustomRaffleItem>
)
