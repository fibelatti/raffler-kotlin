package com.fibelatti.raffler.features.preferences.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.preferences.Preferences
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val preferences by lazy { MutableLiveData<Preferences>() }
    val updateFeedback by lazy { MutableLiveEvent<String>() }
    val totalQuantityError by lazy { MutableLiveEvent<String>() }
    val raffleQuantityError by lazy { MutableLiveEvent<String>() }

    fun getPreferences() {
        start {
            inBackground { preferencesRepository.getPreferences() }
                .onSuccess(preferences::setValue)
                .onFailure(::handleError)
        }
    }

    fun setAppTheme(appTheme: AppConfig.AppTheme) {
        start {
            inBackground { preferencesRepository.setAppTheme(appTheme) }
        }
    }

    fun setAppLanguage(appLanguage: AppConfig.AppLanguage) {
        start {
            inBackground { preferencesRepository.setLanguage(appLanguage) }
        }
    }

    fun setLotteryDefaultValues(quantityAvailable: String, quantityToRaffle: String) {
        validateData(quantityAvailable, quantityToRaffle) { qtyTotal, qtyRaffle ->
            start {
                inBackground { preferencesRepository.setLotteryDefault(qtyTotal, qtyRaffle) }
                    .handleResult()
            }
        }
    }

    fun setPreferredRaffleMode(raffleMode: AppConfig.RaffleMode) {
        start {
            inBackground { preferencesRepository.setPreferredRaffleMode(raffleMode) }
                .handleResult()
        }
    }

    fun setRouletteMusicEnabled(value: Boolean) {
        start {
            inBackground { preferencesRepository.setRouletteMusicEnabled(value) }
                .handleResult()
        }
    }

    fun setRememberRaffledItems(value: Boolean) {
        start {
            inBackground { preferencesRepository.rememberRaffledItems(value) }
                .handleResult()
        }
    }

    fun resetAllHints() {
        start {
            inBackground { preferencesRepository.resetHints() }
                .handleResult()
        }
    }

    private fun Result<Unit>.handleResult() {
        onSuccess {
            getPreferences()
            updateFeedback.postEvent(resourceProvider.getString(R.string.preferences_changes_saved))
        }.onFailure(::handleError)
    }

    private fun validateData(
        quantityAvailable: String,
        quantityToRaffle: String,
        ifValid: (Int, Int) -> Unit
    ) {
        val qtyAvailable = quantityAvailable.takeIf { it.isNotBlank() } ?: "0"
        val qtyToRaffle = quantityToRaffle.takeIf { it.isNotBlank() } ?: "0"

        when {
            !qtyAvailable.isInt() -> {
                totalQuantityError.postEvent(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            !qtyToRaffle.isInt() -> {
                totalQuantityError.postEvent(String.empty())
                raffleQuantityError.postEvent(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            else -> {
                totalQuantityError.postEvent(String.empty())
                raffleQuantityError.postEvent(String.empty())

                ifValid(qtyAvailable.toInt(), qtyToRaffle.toInt())
            }
        }
    }
}
