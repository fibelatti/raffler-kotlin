package com.fibelatti.raffler.features.lottery.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.extension.empty
import com.fibelatti.core.extension.isInt
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LotteryViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val lotteryNumberModelMapper: LotteryNumberModelMapper,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val defaultQuantityAvailable: LiveData<String> get() = _defaultQuantityAvailable
    private val _defaultQuantityAvailable = MutableLiveData<String>()
    val defaultQuantityToRaffle: LiveData<String> get() = _defaultQuantityToRaffle
    private val _defaultQuantityToRaffle = MutableLiveData<String>()
    val showHint: LiveEvent<Unit> get() = _showHint
    private val _showHint = MutableLiveEvent<Unit>()
    val lotteryNumbers: LiveData<List<LotteryNumberModel>> get() = _lotteryNumbers
    private val _lotteryNumbers = MutableLiveData<List<LotteryNumberModel>>()
    val quantityAvailableError: LiveData<String> get() = _quantityAvailableError
    private val _quantityAvailableError = MutableLiveData<String>()
    val quantityToRaffleError: LiveData<String> get() = _quantityToRaffleError
    private val _quantityToRaffleError = MutableLiveData<String>()

    init {
        getDefaults()
        checkForHints()
    }

    fun getLotteryNumbers(quantityAvailable: String, quantityToRaffle: String) {
        launch {
            validateData(quantityAvailable, quantityToRaffle) { totalQty, raffleQty ->
                (1..totalQty).shuffled()
                    .take(raffleQty)
                    .let(lotteryNumberModelMapper::mapList)
                    .let(_lotteryNumbers::postValue)
            }
        }
    }

    fun hintDismissed() {
        launch { preferencesRepository.setLotteryHintDismissed() }
    }

    private fun getDefaults() {
        launch {
            preferencesRepository.getPreferences()
                .onSuccess {
                    _defaultQuantityAvailable.postValue(it.lotteryDefaultQuantityAvailable)
                    _defaultQuantityToRaffle.postValue(it.lotteryDefaultQuantityToRaffle)
                }
        }
    }

    private fun checkForHints() {
        launch {
            if (!preferencesRepository.getLotteryHintDisplayed()) {
                _showHint.postEvent(Unit)
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
                _quantityAvailableError.postValue(
                    resourceProvider.getString(R.string.lottery_quantity_validation_error)
                )
            }
            quantityToRaffle.isBlank() || !quantityToRaffle.isInt() -> {
                _quantityAvailableError.postValue(String.empty())
                _quantityToRaffleError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            else -> {
                _quantityAvailableError.postValue(String.empty())
                _quantityToRaffleError.postValue(String.empty())

                ifValid(quantityAvailable.toInt(), quantityToRaffle.toInt())
            }
        }
    }
}
