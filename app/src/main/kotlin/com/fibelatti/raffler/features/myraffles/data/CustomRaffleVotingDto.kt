package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fibelatti.raffler.core.functional.Mapper
import com.fibelatti.raffler.features.myraffles.CustomRaffleVoting
import javax.inject.Inject

const val CUSTOM_RAFFLE_VOTING_TABLE_NAME = "CustomRaffleVoting"
const val CUSTOM_RAFFLE_VOTING_CUSTOM_RAFFLE_ID_COLUMN_NAME = "customRaffleId"

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
) {
    @Ignore
    constructor() : this(0, "", 0, 0, emptyMap())
}

class CustomRaffleVotingDtoMapper @Inject constructor() :
    Mapper<CustomRaffleVotingDto, CustomRaffleVoting> {
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
