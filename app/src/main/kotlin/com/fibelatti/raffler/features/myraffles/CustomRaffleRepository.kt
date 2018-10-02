package com.fibelatti.raffler.features.myraffles

import com.fibelatti.raffler.core.functional.Result

interface CustomRaffleRepository {
    suspend fun getAllCustomRaffles(): Result<List<CustomRaffle>>

    suspend fun getCustomRaffleById(id: Long): Result<CustomRaffle>

    suspend fun addCustomRaffle(customRaffle: CustomRaffle): Result<Unit>
}
