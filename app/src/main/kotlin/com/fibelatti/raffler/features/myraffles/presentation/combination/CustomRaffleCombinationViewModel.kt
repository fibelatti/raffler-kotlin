package com.fibelatti.raffler.features.myraffles.presentation.combination

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.mapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import javax.inject.Inject
import kotlin.math.min

class CustomRaffleCombinationViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val otherCustomRaffles by lazy { MutableLiveData<List<CustomRaffleModel>>() }
    val pairs by lazy { MutableLiveData<List<CustomRaffleDraftedModel>>() }
    val quantityError by lazy { MutableLiveEvent<String>() }

    fun getPairs(
        firstCustomRaffle: CustomRaffleModel,
        secondCustomRaffle: CustomRaffleModel,
        quantity: String
    ) {
        startInBackground {
            validateData(firstCustomRaffle.includedItems, secondCustomRaffle.items, quantity) { qty ->
                val firstShuffled = firstCustomRaffle.includedItems.shuffled()
                val secondShuffled = secondCustomRaffle.items.shuffled()

                (0 until qty).mapIndexed { _, i ->
                    CustomRaffleDraftedModel(
                        title = resourceProvider.getString(R.string.custom_raffle_combination_pair_title, i + 1),
                        description = "${firstShuffled[i].description}\n${secondShuffled[i].description}"
                    )
                }.let { pairs.postValue(it) }
            }
        }
    }

    fun getCustomRafflesToCombineWith(customRaffle: CustomRaffleModel) {
        startInBackground {
            customRaffleRepository.getAllCustomRaffles()
                .mapCatching { raffles ->
                    raffles.filter { it.id != customRaffle.id }
                        .let(customRaffleModelMapper::mapList)
                }
                .onSuccess(otherCustomRaffles::postValue)
                .onFailure(error::postValue)
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
                quantityError.postEvent(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantity.toInt() > firstOptions.size || quantity.toInt() > secondOptions.size -> {
                quantityError.postEvent(resourceProvider.getString(
                    R.string.custom_raffle_combination_invalid_quantity_too_many,
                    min(firstOptions.size, secondOptions.size)
                ))
            }
            quantity.toInt() < 1 -> {
                quantityError.postEvent(resourceProvider.getString(R.string.custom_raffle_combination_invalid_quantity_too_few))
            }
            else -> {
                quantityError.postEvent(String.empty())

                ifValid(quantity.toInt())
            }
        }
    }
}
