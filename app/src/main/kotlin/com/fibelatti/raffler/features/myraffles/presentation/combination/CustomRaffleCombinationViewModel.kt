package com.fibelatti.raffler.features.myraffles.presentation.combination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.extension.empty
import com.fibelatti.core.extension.isInt
import com.fibelatti.core.functional.mapCatching
import com.fibelatti.core.functional.onFailure
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min

class CustomRaffleCombinationViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val otherCustomRaffles: LiveData<List<CustomRaffleModel>> get() = _otherCustomRaffles
    private val _otherCustomRaffles = MutableLiveData<List<CustomRaffleModel>>()
    val pairs: LiveData<List<CustomRaffleDraftedModel>> get() = _pairs
    private val _pairs = MutableLiveData<List<CustomRaffleDraftedModel>>()
    val quantityError: LiveEvent<String> get() = _quantityError
    private val _quantityError = MutableLiveEvent<String>()

    fun getCombinations(
        firstCustomRaffle: CustomRaffleModel,
        secondCustomRaffle: CustomRaffleModel,
        quantity: String
    ) {
        launch {
            validateData(
                firstCustomRaffle.includedItems,
                secondCustomRaffle.items,
                quantity
            ) { qty ->
                val quantityPerGroup = ceil(
                    (firstCustomRaffle.includedItems.size + secondCustomRaffle.items.size) / qty.toDouble()
                ).toInt()

                val combinations = (firstCustomRaffle.includedItems + secondCustomRaffle.items)
                    .shuffled()
                    .chunked(quantityPerGroup)
                    .mapIndexed { index, chunk ->
                        CustomRaffleDraftedModel(
                            title = resourceProvider.getString(
                                R.string.custom_raffle_combination_pair_title,
                                index + 1
                            ),
                            description = chunk.joinToString("\n") { it.description }
                        )
                    }

                _pairs.postValue(combinations)
            }
        }
    }

    fun getCustomRafflesToCombineWith() {
        launch {
            customRaffleRepository.getAllCustomRaffles()
                .mapCatching(customRaffleModelMapper::mapList)
                .onSuccess(_otherCustomRaffles::postValue)
                .onFailure(::handleError)
        }
    }

    private fun validateData(
        firstOptions: List<CustomRaffleItemModel>,
        secondOptions: List<CustomRaffleItemModel>,
        quantity: String,
        ifValid: (quantity: Int) -> Unit
    ) {
        when {
            quantity.isBlank() || !quantity.isInt() -> {
                _quantityError.postEvent(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantity.toInt() > firstOptions.size || quantity.toInt() > secondOptions.size -> {
                _quantityError.postEvent(
                    resourceProvider.getString(
                        R.string.custom_raffle_combination_invalid_quantity_too_many,
                        min(firstOptions.size, secondOptions.size)
                    )
                )
            }
            quantity.toInt() < 1 -> {
                _quantityError.postEvent(
                    resourceProvider.getString(R.string.custom_raffle_combination_invalid_quantity_too_few)
                )
            }
            else -> {
                _quantityError.postEvent(String.empty())
                ifValid(quantity.toInt())
            }
        }
    }
}
