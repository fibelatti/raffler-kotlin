package com.fibelatti.raffler.features.myraffles.presentation.common

import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.core.functional.TwoWayMapper
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import java.text.Collator
import javax.inject.Inject

data class CustomRaffleModel(
    val id: Long,
    val description: String,
    val items: List<CustomRaffleItemModel>
) : BaseViewType {

    companion object {
        @JvmStatic
        val VIEW_TYPE = CustomRaffleModel::class.hashCode()

        fun empty() = CustomRaffleModel(0, "", mutableListOf())
    }

    override fun getViewType(): Int = VIEW_TYPE

    val includedItems
        get() = items.filter(CustomRaffleItemModel::included)
    val includedItemsIndex
        get() = items.mapIndexedNotNull { index, customRaffleItemModel ->
            index.takeIf { customRaffleItemModel.included }
        }
    val itemSelectionIsValid
        get() = includedItems.size > 1
}

class CustomRaffleModelMapper @Inject constructor(
    private val customRaffleItemModelMapper: CustomRaffleItemModelMapper,
    private val usCollator: Collator
) : TwoWayMapper<CustomRaffle, CustomRaffleModel> {

    override fun map(param: CustomRaffle): CustomRaffleModel {
        return with(param) {
            CustomRaffleModel(
                id,
                description,
                items.sortedWith(
                    Comparator { first, second ->
                        usCollator.compare(first.description, second.description)
                    }
                ).map(customRaffleItemModelMapper::map).toMutableList()
            )
        }
    }

    override fun mapReverse(param: CustomRaffleModel): CustomRaffle {
        return with(param) {
            CustomRaffle(id, description, items = customRaffleItemModelMapper.mapListReverse(items))
        }
    }
}
