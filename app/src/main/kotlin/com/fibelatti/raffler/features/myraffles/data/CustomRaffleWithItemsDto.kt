package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Embedded
import androidx.room.Relation
import com.fibelatti.core.functional.TwoWayMapper
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import javax.inject.Inject

class CustomRaffleWithItemsDto {

    @Embedded
    var customRaffleDto: CustomRaffleDto = CustomRaffleDto(id = 0, description = "")

    @Relation(
        parentColumn = CUSTOM_RAFFLE_ID_COLUMN_NAME,
        entityColumn = CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME
    )
    var items: List<CustomRaffleItemDto> = listOf()
}

class CustomRaffleWithItemsDtoMapper @Inject constructor(
    private val customRaffleItemDtoMapper: CustomRaffleItemDtoMapper
) : TwoWayMapper<CustomRaffleWithItemsDto, CustomRaffle> {

    override fun map(param: CustomRaffleWithItemsDto): CustomRaffle {
        return with(param) {
            CustomRaffle(
                id = customRaffleDto.id,
                description = customRaffleDto.description,
                items = customRaffleItemDtoMapper.mapList(items)
            )
        }
    }

    override fun mapReverse(param: CustomRaffle): CustomRaffleWithItemsDto {
        return with(param) {
            CustomRaffleWithItemsDto().apply {
                customRaffleDto = CustomRaffleDto(id, description)
                items = customRaffleItemDtoMapper.mapListReverse(param.items)
            }
        }
    }
}
