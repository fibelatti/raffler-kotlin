package com.fibelatti.raffler.features.myraffles.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.randomize.Randomize
import javax.inject.Inject

class CustomRaffleRandomWinnersViewModel @Inject constructor(
    private val randomize: Randomize,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val randomWinners by lazy { MutableLiveData<List<CustomRaffleDraftedModel>>() }
    val quantityError by lazy { MutableLiveData<String>() }

    fun getRandomWinners(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) {
            start {
                inBackground {
                    randomize(Randomize.Params(options.size, quantity.toInt()))
                        .flatMapCatching { winners ->
                            options.filterIndexed { index, _ -> index in winners }
                                .mapIndexed { index, item ->
                                    CustomRaffleDraftedModel(
                                        title = resourceProvider.getString(R.string.custom_raffle_random_winners_item_title, index + 1),
                                        description = item.description
                                    )
                                }
                        }
                }.either(
                    { error.value = it },
                    { randomWinners.value = it }
                )
            }
        }
    }

    private fun validateData(options: List<CustomRaffleItemModel>, quantity: String, ifValid: () -> Unit) {
        when {
            quantity.isBlank() || !quantity.isInt() -> {
                quantityError.value = resourceProvider.getString(R.string.lottery_quantity_validation_error)
            }
            quantity.toInt() > options.size - 1 -> {
                quantityError.value = resourceProvider.getString(
                    R.string.custom_raffle_random_winners_invalid_quantity_too_many,
                    options.size - 1
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
}
