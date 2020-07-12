package com.fibelatti.raffler.features.myraffles.presentation.randomwinners

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.extension.empty
import com.fibelatti.core.extension.isInt
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.RememberRaffled
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomRaffleRandomWinnersViewModel @Inject constructor(
    private val rememberRaffled: RememberRaffled,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val randomWinners: LiveData<List<CustomRaffleDraftedModel>> get() = _randomWinners
    private val _randomWinners = MutableLiveData<List<CustomRaffleDraftedModel>>()
    val quantityError: LiveData<String> get() = _quantityError
    private val _quantityError = MutableLiveData<String>()

    fun getRandomWinners(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) { qty ->
            launch {
                options.shuffled()
                    .take(qty)
                    .also { raffledItems ->
                        raffledItems.forEach { rememberRaffled(RememberRaffled.Params(it, included = false)) }
                    }
                    .mapIndexed { index, item ->
                        CustomRaffleDraftedModel(
                            title = resourceProvider.getString(
                                R.string.custom_raffle_random_winners_item_title,
                                index + 1
                            ),
                            description = item.description
                        )
                    }
                    .let(_randomWinners::postValue)
            }
        }
    }

    private fun validateData(
        options: List<CustomRaffleItemModel>,
        quantity: String,
        ifValid: (quantity: Int) -> Unit
    ) {
        when {
            quantity.isBlank() || !quantity.isInt() -> {
                _quantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantity.toInt() > options.size - 1 -> {
                _quantityError.postValue(resourceProvider.getString(
                    R.string.custom_raffle_random_winners_invalid_quantity_too_many,
                    options.size - 1
                ))
            }
            quantity.toInt() < 1 -> {
                _quantityError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_random_winners_invalid_quantity_too_few)
                )
            }
            else -> {
                _quantityError.postValue(String.empty())
                ifValid(quantity.toInt())
            }
        }
    }
}
