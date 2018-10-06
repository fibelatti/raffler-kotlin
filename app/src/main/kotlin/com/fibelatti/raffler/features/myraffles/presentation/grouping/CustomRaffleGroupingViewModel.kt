package com.fibelatti.raffler.features.myraffles.presentation.grouping

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.batchesOf
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import javax.inject.Inject

class CustomRaffleGroupingViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val groups by lazy { MutableLiveData<List<CustomRaffleDraftedModel>>() }
    val quantityError by lazy { MutableLiveData<String>() }

    fun getGroupsByQuantity(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            start {
                groups.value = inBackground { createBatches(options, Math.ceil(options.size.toDouble() / quantity.toInt()).toInt()) }
            }
        }
    }

    fun getGroupsByItemQuantity(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            start {
                groups.value = inBackground {createBatches(options, quantity.toInt()) }
            }
        }
    }

    private fun createBatches(options: List<CustomRaffleItemModel>, quantity: Int): List<CustomRaffleDraftedModel> {
        return options.toList().shuffled().batchesOf(quantity)
            .mapIndexed { index, item ->
                CustomRaffleDraftedModel(
                    title = resourceProvider.getString(R.string.custom_raffle_grouping_item_title, index + 1),
                    description = item.joinToString(separator = "\n") { it.description }
                )
            }
    }

    private fun validateData(options: List<CustomRaffleItemModel>, quantity: String, ifValid: () -> Unit) {
        when {
            quantity.isBlank() || !quantity.isInt() -> {
                quantityError.value = resourceProvider.getString(R.string.lottery_quantity_validation_error)
            }
            quantity.toInt() > options.maxQuantity -> {
                quantityError.value = resourceProvider.getString(
                    R.string.custom_raffle_random_winners_invalid_quantity_too_many,
                    options.maxQuantity
                )
            }
            quantity.toInt() < 1 -> {
                quantityError.value = resourceProvider.getString(R.string.custom_raffle_random_winners_invalid_quantity_too_few)
            }
            else -> {
                quantityError.value = String.empty()

                ifValid()
            }
        }
    }

    private val List<CustomRaffleItemModel>.maxQuantity: Int
        get() = Math.max(Math.ceil(size.toDouble() / 2), 2.0).toInt()
}
