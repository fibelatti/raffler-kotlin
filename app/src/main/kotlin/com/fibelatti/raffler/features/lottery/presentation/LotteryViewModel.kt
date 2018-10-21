package com.fibelatti.raffler.features.lottery.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class LotteryViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val lotteryNumberModelMapper: LotteryNumberModelMapper,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val defaultQuantityAvailable by lazy { MutableLiveData<String>() }
    val defaultQuantityToRaffle by lazy { MutableLiveData<String>() }
    val showHint by lazy { MutableLiveEvent<Unit>() }
    val lotteryNumbers by lazy { MutableLiveData<List<LotteryNumberModel>>() }
    val quantityAvailableError by lazy { MutableLiveData<String>() }
    val quantityToRaffleError by lazy { MutableLiveData<String>() }

    init {
        getDefaults()
        checkForHints()
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

    fun hintDismissed() {
        startInBackground { preferencesRepository.setLotteryHintDismissed() }
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

    private fun checkForHints() {
        startInBackground {
            if (!preferencesRepository.getLotteryHintDisplayed()) {
                showHint.postEvent(Unit)
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
