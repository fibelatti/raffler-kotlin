package com.fibelatti.raffler.features.myraffles.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fibelatti.core.functional.TwoWayMapper
import com.fibelatti.raffler.features.myraffles.CustomRaffleVoting
import javax.inject.Inject

const val CUSTOM_RAFFLE_VOTING_TABLE_NAME = "CustomRaffleVoting"
const val CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME = "customRaffleId"

@Keep
@Entity(
    tableName = CUSTOM_RAFFLE_VOTING_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = CustomRaffleDto::class,
        parentColumns = [CUSTOM_RAFFLE_ID_COLUMN_NAME],
        childColumns = [CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME],
        onDelete = ForeignKey.CASCADE)
    ]
)
data class CustomRaffleVotingDto(
    @PrimaryKey
    val customRaffleId: Long,
    val description: String,
    val pin: Int,
    val totalVotes: Int,
    val votes: Map<String, Int>
)

class CustomRaffleVotingDtoMapper @Inject constructor() : TwoWayMapper<CustomRaffleVotingDto, CustomRaffleVoting> {

    override fun map(param: CustomRaffleVotingDto): CustomRaffleVoting {
        return with(param) {
            CustomRaffleVoting(customRaffleId, description, pin, totalVotes, votes)
        }
    }

    override fun mapReverse(param: CustomRaffleVoting): CustomRaffleVotingDto {
        return with(param) {
            CustomRaffleVotingDto(customRaffleId, description, pin, totalVotes, votes)
        }
    }
}
