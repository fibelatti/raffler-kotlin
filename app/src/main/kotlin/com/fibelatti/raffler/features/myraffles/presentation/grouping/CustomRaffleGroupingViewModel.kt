package com.fibelatti.raffler.features.myraffles.presentation.grouping

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

class CustomRaffleGroupingViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val groups by lazy { MutableLiveData<List<CustomRaffleDraftedModel>>() }
    val quantityError by lazy { MutableLiveData<String>() }

    fun getGroupsByQuantity(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            startInBackground {
                createBatches(options, ceil(options.size.toDouble() / quantity.toInt()).toInt())
                    .let(groups::postValue)
            }
        }
    }

    fun getGroupsByItemQuantity(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            startInBackground {
                createBatches(options, quantity.toInt())
                    .let(groups::postValue)
            }
        }
    }

    private fun createBatches(options: List<CustomRaffleItemModel>, quantity: Int): List<CustomRaffleDraftedModel> {
        return options.toList().shuffled().chunked(quantity)
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
                quantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantity.toInt() > options.maxQuantity -> {
                quantityError.postValue(resourceProvider.getString(
                    R.string.custom_raffle_random_winners_invalid_quantity_too_many,
                    options.maxQuantity
                ))
            }
            quantity.toInt() < 1 -> {
                quantityError.postValue(resourceProvider.getString(R.string.custom_raffle_random_winners_invalid_quantity_too_few))
            }
            else -> {
                quantityError.postValue(String.empty())

                ifValid()
            }
        }
    }

    private val List<CustomRaffleItemModel>.maxQuantity: Int
        get() = max(Math.ceil(size.toDouble() / 2), 2.0).toInt()
}
