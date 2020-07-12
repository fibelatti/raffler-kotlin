package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.fibelatti.core.functional.Result
import com.fibelatti.raffler.core.functional.resultFrom
import com.fibelatti.raffler.features.myraffles.CustomRaffleVoting
import com.fibelatti.raffler.features.myraffles.CustomRaffleVotingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomRaffleVotingDataSource @Inject constructor(
    private val customRaffleVotingDao: CustomRaffleVotingDao,
    private val customRaffleVotingDtoMapper: CustomRaffleVotingDtoMapper
) : CustomRaffleVotingRepository {

    override suspend fun getCustomRaffleVoting(customRaffleId: Long): Result<CustomRaffleVoting?> =
        resultFrom {
            withContext(Dispatchers.IO) {
                customRaffleVotingDao.getVotingForCustomRaffle(customRaffleId)
            }?.let(customRaffleVotingDtoMapper::map)
        }

    override suspend fun saveCustomRaffleVoting(customRaffleVoting: CustomRaffleVoting): Result<Unit> =
        resultFrom {
            withContext(Dispatchers.IO) {
                customRaffleVotingDtoMapper.mapReverse(customRaffleVoting)
                    .let(customRaffleVotingDao::saveVoting)
            }
        }

    override suspend fun deleteCustomRaffleVoting(customRaffleId: Long): Result<Unit> =
        resultFrom {
            withContext(Dispatchers.IO) {
                customRaffleVotingDao.deleteVoting(customRaffleId)
            }
        }
}

@Dao
interface CustomRaffleVotingDao {

    @Query("select * from $CUSTOM_RAFFLE_VOTING_TABLE_NAME " +
        "where $CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME = :customRaffleId ")
    fun getVotingForCustomRaffle(customRaffleId: Long): CustomRaffleVotingDto?

    @Insert(onConflict = REPLACE)
    fun saveVoting(customRaffleVotingDto: CustomRaffleVotingDto)

    @Query("delete from $CUSTOM_RAFFLE_VOTING_TABLE_NAME " +
        "where $CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME = :customRaffleId ")
    fun deleteVoting(customRaffleId: Long)
}
