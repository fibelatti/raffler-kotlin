package com.fibelatti.raffler.features.myraffles.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fibelatti.core.functional.TwoWayMapper
import com.fibelatti.raffler.features.myraffles.CustomRaffleItem
import javax.inject.Inject

const val CUSTOM_RAFFLE_ITEM_TABLE_NAME = "CustomRaffleItem"
const val CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME = "customRaffleId"

@Keep
@Entity(
    tableName = CUSTOM_RAFFLE_ITEM_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = CustomRaffleDto::class,
        parentColumns = [CUSTOM_RAFFLE_ID_COLUMN_NAME],
        childColumns = [CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME],
        onDelete = CASCADE)
    ],
    indices = [Index(CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME)]
)
data class CustomRaffleItemDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val customRaffleId: Long,
    val description: String,
    val included: Boolean
)

class CustomRaffleItemDtoMapper @Inject constructor() : TwoWayMapper<CustomRaffleItemDto, CustomRaffleItem> {

    override fun map(param: CustomRaffleItemDto): CustomRaffleItem {
        return with(param) { CustomRaffleItem(id, customRaffleId, description, included) }
    }

    override fun mapReverse(param: CustomRaffleItem): CustomRaffleItemDto {
        return with(param) { CustomRaffleItemDto(id, customRaffleId, description, included) }
    }
}
