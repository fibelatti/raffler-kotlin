package com.fibelatti.raffler.features.myraffles.presentation

import com.fibelatti.raffler.core.functional.Mapper
import com.fibelatti.raffler.core.platform.BaseViewType
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import javax.inject.Inject

data class CustomRaffleModel(
    val id: Long,
    val description: String,
    val items: List<CustomRaffleItemModel>
) : BaseViewType {
    companion object {
        @JvmStatic
        val VIEW_TYPE = CustomRaffleModel::class.hashCode()
    }

    override fun getViewType(): Int = VIEW_TYPE
}

class CustomRaffleModelMapper @Inject constructor(
    private val customRaffleItemModelMapper: CustomRaffleItemModelMapper
) : Mapper<CustomRaffle, CustomRaffleModel> {
    override fun map(param: CustomRaffle): CustomRaffleModel {
        return with(param) {
            CustomRaffleModel(id, description, items = customRaffleItemModelMapper.map(items))
        }
    }

    override fun mapReverse(param: CustomRaffleModel): CustomRaffle {
        return with(param) {
            CustomRaffle(id, description, items = customRaffleItemModelMapper.mapReverse(items))
        }
    }
}
