package com.fibelatti.raffler.features.myraffles

import com.fibelatti.core.functional.Result

interface CustomRaffleRepository {

    suspend fun getAllCustomRaffles(): Result<List<CustomRaffle>>

    suspend fun getCustomRaffleById(id: Long): Result<CustomRaffle>

    suspend fun saveCustomRaffle(customRaffle: CustomRaffle): Result<CustomRaffle>

    suspend fun updateCustomRaffleItem(customRaffleItem: CustomRaffleItem): Result<Unit>

    suspend fun deleteCustomRaffleById(id: Long): Result<Unit>
}
