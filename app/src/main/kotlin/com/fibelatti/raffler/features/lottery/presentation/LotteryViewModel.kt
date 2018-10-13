package com.fibelatti.raffler.features.lottery.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class LotteryViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val lotteryNumberModelMapper: LotteryNumberModelMapper,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val defaultQuantityAvailable by lazy { MutableLiveData<String>() }
    val defaultQuantityToRaffle by lazy { MutableLiveData<String>() }
    val lotteryNumbers by lazy { MutableLiveData<List<LotteryNumberModel>>() }
    val quantityAvailableError by lazy { MutableLiveData<String>() }
    val quantityToRaffleError by lazy { MutableLiveData<String>() }

    init {
        getDefaults()
    }

    fun getLotteryNumbers(quantityAvailable: String, quantityToRaffle: String) {
        startInBackground {
            validateData(quantityAvailable, quantityToRaffle) { totalQty, raffleQty ->
                (1..totalQty).shuffled()
                    .take(raffleQty)
                    .let(lotteryNumberModelMapper::mapList)
                    .also(lotteryNumbers::postValue)
            }
        }
    }

    private fun getDefaults() {
        startInBackground {
            preferencesRepository.getPreferences()
                .onSuccess {
                    defaultQuantityAvailable.postValue(it.lotteryDefaultQuantityAvailable)
                    defaultQuantityToRaffle.postValue(it.lotteryDefaultQuantityToRaffle)
                }
        }
    }

    private fun validateData(
        quantityAvailable: String,
        quantityToRaffle: String,
        ifValid: (quantityAvailable: Int, quantityToRaffle: Int) -> Unit
    ) {
        when {
            quantityAvailable.isBlank() || !quantityAvailable.isInt() -> {
                quantityAvailableError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantityToRaffle.isBlank() || !quantityToRaffle.isInt() -> {
                quantityAvailableError.postValue(String.empty())
                quantityToRaffleError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            else -> {
                quantityAvailableError.postValue(String.empty())
                quantityToRaffleError.postValue(String.empty())

                ifValid(quantityAvailable.toInt(), quantityToRaffle.toInt())
            }
        }
    }
}
