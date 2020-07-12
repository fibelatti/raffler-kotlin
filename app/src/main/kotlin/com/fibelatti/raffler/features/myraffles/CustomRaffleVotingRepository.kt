package com.fibelatti.raffler.features.myraffles

import com.fibelatti.core.functional.Result

interface CustomRaffleVotingRepository {

    suspend fun getCustomRaffleVoting(customRaffleId: Long): Result<CustomRaffleVoting?>

    suspend fun saveCustomRaffleVoting(customRaffleVoting: CustomRaffleVoting): Result<Unit>

    suspend fun deleteCustomRaffleVoting(customRaffleId: Long): Result<Unit>
}
