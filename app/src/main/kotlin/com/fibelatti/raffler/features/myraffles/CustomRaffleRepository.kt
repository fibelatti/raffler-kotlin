package com.fibelatti.raffler.features.myraffles

import com.fibelatti.raffler.core.functional.Either

interface CustomRaffleRepository {
    suspend fun getAllCustomRaffles(): Either<Throwable, List<CustomRaffle>>

    suspend fun getCustomRaffleById(id: Long): Either<Throwable, CustomRaffle>

    suspend fun addCustomRaffle(customRaffle: CustomRaffle): Either<Throwable, Unit>
}
