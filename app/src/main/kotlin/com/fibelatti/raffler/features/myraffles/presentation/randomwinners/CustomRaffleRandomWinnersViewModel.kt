package com.fibelatti.raffler.features.myraffles.presentation.randomwinners

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.RememberRaffled
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import javax.inject.Inject

class CustomRaffleRandomWinnersViewModel @Inject constructor(
    private val rememberRaffled: RememberRaffled,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val randomWinners by lazy { MutableLiveData<List<CustomRaffleDraftedModel>>() }
    val quantityError by lazy { MutableLiveData<String>() }

    fun getRandomWinners(options: List<CustomRaffleItemModel>, quantity: String) {
        validateData(options, quantity) { qty ->
            startInBackground {
                options.shuffled()
                    .take(qty)
                    .also { raffledItems ->
                        raffledItems.forEach { rememberRaffled(RememberRaffled.Params(it, included = false)) }
                    }
                    .mapIndexed { index, item ->
                        CustomRaffleDraftedModel(
                            title = resourceProvider.getString(R.string.custom_raffle_random_winners_item_title, index + 1),
                            description = item.description
                        )
                    }
                    .let(randomWinners::postValue)
            }
        }
    }

    private fun validateData(options: List<CustomRaffleItemModel>, quantity: String, ifValid: (quantity: Int) -> Unit) {
        when {
            quantity.isBlank() || !quantity.isInt() -> {
                quantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantity.toInt() > options.size - 1 -> {
                quantityError.postValue(resourceProvider.getString(
                    R.string.custom_raffle_random_winners_invalid_quantity_too_many,
                    options.size - 1
                ))
            }
            quantity.toInt() < 1 -> {
                quantityError.postValue(resourceProvider.getString(R.string.custom_raffle_random_winners_invalid_quantity_too_few))
            }
            else -> {
                quantityError.postValue(String.empty())

                ifValid(quantity.toInt())
            }
        }
    }
}
