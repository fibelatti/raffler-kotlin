package com.fibelatti.raffler.features.myraffles.presentation.grouping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.extension.empty
import com.fibelatti.core.extension.isInt
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

class CustomRaffleGroupingViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val groups: LiveData<List<CustomRaffleDraftedModel>> get() = _groups
    private val _groups = MutableLiveData<List<CustomRaffleDraftedModel>>()
    val quantityError: LiveData<String> get() = _quantityError
    private val _quantityError = MutableLiveData<String>()

    fun getGroupsByQuantity(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            launch {
                createBatches(options, ceil(options.size.toDouble() / quantity.toInt()).toInt())
                    .let(_groups::postValue)
            }
        }
    }

    fun getGroupsByItemQuantity(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            launch {
                createBatches(options, quantity.toInt())
                    .let(_groups::postValue)
            }
        }
    }

    private fun createBatches(options: List<CustomRaffleItemModel>, quantity: Int): List<CustomRaffleDraftedModel> {
        return options.shuffled().chunked(quantity).mapIndexed { index, item ->
            CustomRaffleDraftedModel(
                title = resourceProvider.getString(R.string.custom_raffle_grouping_item_title, index + 1),
                description = item.joinToString(separator = "\n") { it.description }
            )
        }
    }

    private fun validateData(options: List<CustomRaffleItemModel>, quantity: String, ifValid: () -> Unit) {
        when {
            quantity.isBlank() || !quantity.isInt() -> {
                _quantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantity.toInt() > options.maxQuantity -> {
                _quantityError.postValue(resourceProvider.getString(
                    R.string.custom_raffle_random_winners_invalid_quantity_too_many,
                    options.maxQuantity
                ))
            }
            quantity.toInt() < 1 -> {
                _quantityError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_random_winners_invalid_quantity_too_few)
                )
            }
            else -> {
                _quantityError.postValue(String.empty())
                ifValid()
            }
        }
    }

    private val List<CustomRaffleItemModel>.maxQuantity: Int
        get() = max(ceil(size.toDouble() / 2), 2.0).toInt()
}
