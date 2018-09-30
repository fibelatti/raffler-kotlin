package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

const val CUSTOM_RAFFLE_TABLE_NAME = "CustomRaffle"
const val CUSTOM_RAFFLE_ID_COLUMN_NAME = "id"

@Entity(tableName = CUSTOM_RAFFLE_TABLE_NAME)
data class CustomRaffleDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val description: String
) {
    @Ignore
    constructor() : this(0, "")
}
