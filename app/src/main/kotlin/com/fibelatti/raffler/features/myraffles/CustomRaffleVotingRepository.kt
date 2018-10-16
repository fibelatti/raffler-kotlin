package com.fibelatti.raffler.features.myraffles

import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.features.CustomRaffleVoting

interface CustomRaffleVotingRepository {
    suspend fun getCustomRaffleVoting(customRaffleId: Long): Result<CustomRaffleVoting>

    suspend fun saveCustomRaffleVoting(customRaffleVoting: CustomRaffleVoting): Result<Unit>

    suspend fun deleteCustomRaffleVoting(customRaffleId: Long): Result<Unit>
}
