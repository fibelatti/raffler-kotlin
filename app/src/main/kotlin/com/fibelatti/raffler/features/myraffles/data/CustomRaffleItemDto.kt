package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fibelatti.raffler.core.functional.Mapper
import com.fibelatti.raffler.features.myraffles.CustomRaffleItem
import javax.inject.Inject

const val CUSTOM_RAFFLE_ITEM_TABLE_NAME = "CustomRaffleItem"
const val CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME = "customRaffleId"

@Entity(
    tableName = CUSTOM_RAFFLE_ITEM_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = CustomRaffleDto::class,
        parentColumns = [CUSTOM_RAFFLE_ID_COLUMN_NAME],
        childColumns = [CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME],
        onDelete = CASCADE)
    ]
)
data class CustomRaffleItemDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val customRaffleId: Long,
    val description: String,
    val included: Boolean
) {
    @Ignore
    constructor() : this(0, 0, "", true)
}

class CustomRaffleItemDtoMapper @Inject constructor() : Mapper<CustomRaffleItemDto, CustomRaffleItem> {
    override fun map(param: CustomRaffleItemDto): CustomRaffleItem {
        return with(param) { CustomRaffleItem(id, customRaffleId, description, included) }
    }

    override fun mapReverse(param: CustomRaffleItem): CustomRaffleItemDto {
        return with(param) { CustomRaffleItemDto(id, customRaffleId, description, included) }
    }
}
