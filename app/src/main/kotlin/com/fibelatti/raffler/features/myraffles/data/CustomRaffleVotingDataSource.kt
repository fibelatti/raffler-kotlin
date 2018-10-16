package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.catching
import com.fibelatti.raffler.features.CustomRaffleVoting
import com.fibelatti.raffler.features.myraffles.CustomRaffleVotingRepository
import javax.inject.Inject

class CustomRaffleVotingDataSource @Inject constructor(
    private val customRaffleVotingDao: CustomRaffleVotingDao,
    private val customRaffleVotingDtoMapper: CustomRaffleVotingDtoMapper
) : CustomRaffleVotingRepository {
    override suspend fun getCustomRaffleVoting(customRaffleId: Long): Result<CustomRaffleVoting> =
        catching {
            customRaffleVotingDao.getVotingForCustomRaffle(customRaffleId)
                .let(customRaffleVotingDtoMapper::map)
        }

    override suspend fun saveCustomRaffleVoting(customRaffleVoting: CustomRaffleVoting): Result<Unit> =
        catching {
            customRaffleVotingDtoMapper.mapReverse(customRaffleVoting)
                .let(customRaffleVotingDao::saveVoting)
        }

    override suspend fun deleteCustomRaffleVoting(customRaffleId: Long): Result<Unit> =
        catching { customRaffleVotingDao.deleteVoting(customRaffleId) }
}

@Dao
interface CustomRaffleVotingDao {

    @Query("select * from $CUSTOM_RAFFLE_VOTING_TABLE_NAME where $CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME = :customRaffleId ")
    fun getVotingForCustomRaffle(customRaffleId: Long): CustomRaffleVotingDto

    @Insert(onConflict = REPLACE)
    fun saveVoting(customRaffleVotingDto: CustomRaffleVotingDto)

    @Query("delete from $CUSTOM_RAFFLE_VOTING_TABLE_NAME where $CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME = :customRaffleId ")
    fun deleteVoting(customRaffleId: Long)
}
