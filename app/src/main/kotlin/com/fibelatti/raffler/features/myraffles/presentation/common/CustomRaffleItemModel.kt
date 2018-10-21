package com.fibelatti.raffler.features.myraffles.presentation.common

import com.fibelatti.raffler.core.functional.Mapper
import com.fibelatti.raffler.features.myraffles.CustomRaffleItem
import javax.inject.Inject

data class CustomRaffleItemModel(
    val id: Long,
    val customRaffleId: Long,
    val description: String,
    var included: Boolean
) {
    companion object {
        fun empty() = CustomRaffleItemModel(0, 0, "", true)
    }
}

class CustomRaffleItemModelMapper @Inject constructor() : Mapper<CustomRaffleItem, CustomRaffleItemModel> {
    override fun map(param: CustomRaffleItem): CustomRaffleItemModel {
        return with(param) {
            CustomRaffleItemModel(id, customRaffleId, description, included)
        }
    }

    override fun mapReverse(param: CustomRaffleItemModel): CustomRaffleItem {
        return with(param) {
            CustomRaffleItem(id, customRaffleId, description, included)
        }
    }
}
