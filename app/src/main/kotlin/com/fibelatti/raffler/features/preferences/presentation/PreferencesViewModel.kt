package com.fibelatti.raffler.features.preferences.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.extension.empty
import com.fibelatti.core.extension.isInt
import com.fibelatti.core.functional.Result
import com.fibelatti.core.functional.onFailure
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.preferences.Preferences
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val preferences: LiveData<Preferences> get() = _preferences
    private val _preferences = MutableLiveData<Preferences>()
    val updateFeedback: LiveEvent<String> get() = _updateFeedback
    private val _updateFeedback = MutableLiveEvent<String>()
    val totalQuantityError: LiveEvent<String> get() = _totalQuantityError
    private val _totalQuantityError = MutableLiveEvent<String>()
    val raffleQuantityError: LiveEvent<String> get() = _raffleQuantityError
    private val _raffleQuantityError = MutableLiveEvent<String>()

    fun getPreferences() {
        launch {
            preferencesRepository.getPreferences()
                .onSuccess(_preferences::postValue)
                .onFailure(::handleError)
        }
    }

    fun setAppTheme(appTheme: AppConfig.AppTheme) {
        launch { preferencesRepository.setAppTheme(appTheme) }
    }

    fun setAppLanguage(appLanguage: AppConfig.AppLanguage) {
        launch { preferencesRepository.setLanguage(appLanguage) }
    }

    fun setLotteryDefaultValues(quantityAvailable: String, quantityToRaffle: String) {
        validateData(quantityAvailable, quantityToRaffle) { qtyTotal, qtyRaffle ->
            launch {
                preferencesRepository.setLotteryDefault(qtyTotal, qtyRaffle)
                    .handleResult()
            }
        }
    }

    fun setPreferredRaffleMode(raffleMode: AppConfig.RaffleMode) {
        launch {
            preferencesRepository.setPreferredRaffleMode(raffleMode)
                .handleResult()
        }
    }

    fun setRouletteMusicEnabled(value: Boolean) {
        launch {
            preferencesRepository.setRouletteMusicEnabled(value)
                .handleResult()
        }
    }

    fun setRememberRaffledItems(value: Boolean) {
        launch {
            preferencesRepository.rememberRaffledItems(value)
                .handleResult()
        }
    }

    fun resetAllHints() {
        launch {
            preferencesRepository.resetHints()
                .handleResult()
        }
    }

    private fun Result<Unit>.handleResult() {
        onSuccess {
            getPreferences()
            _updateFeedback.postEvent(resourceProvider.getString(R.string.preferences_changes_saved))
        }.onFailure(::handleError)
    }

    private fun validateData(
        quantityAvailable: String,
        quantityToRaffle: String,
        ifValid: (Int, Int) -> Unit
    ) {
        val qtyAvailable = quantityAvailable.ifBlank { "0"  }
        val qtyToRaffle = quantityToRaffle.ifBlank { "0"  }

        when {
            !qtyAvailable.isInt() -> {
                _totalQuantityError.postEvent(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            !qtyToRaffle.isInt() -> {
                _totalQuantityError.postEvent(String.empty())
                _raffleQuantityError.postEvent(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            else -> {
                _totalQuantityError.postEvent(String.empty())
                _raffleQuantityError.postEvent(String.empty())

                ifValid(qtyAvailable.toInt(), qtyToRaffle.toInt())
            }
        }
    }
}
