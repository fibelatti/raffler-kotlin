package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.extension.orZero
import com.fibelatti.raffler.core.functional.mapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.platform.setEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.RememberRaffled
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class CustomRaffleDetailsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val rememberRaffled: RememberRaffled,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    private val rememberRaffledItems by lazy { MutableLiveData<Boolean>() }
    private val customRaffleCount by lazy { MutableLiveData<Int>() }
    val preferredRaffleMode by lazy { MutableLiveData<AppConfig.RaffleMode>() }
    val rouletteMusicEnabled by lazy { MutableLiveData<Boolean>() }
    val customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }
    val showHint by lazy { MutableLiveEvent<Unit>() }
    val invalidSelectionError by lazy { MutableLiveEvent<String>() }
    val showModeSelector by lazy { MutableLiveEvent<Unit>() }
    val showPreferredRaffleMode by lazy { MutableLiveEvent<AppConfig.RaffleMode>() }
    val itemsRemaining by lazy { MutableLiveEvent<Int>() }

    init {
        checkForHints()
        getCustomRaffleCount()
    }

    fun getCustomRaffleById(id: Long?) {
        start {
            getPreferences()

            callInBackground {
                customRaffleRepository.getCustomRaffleById(id.orZero())
                    .mapCatching(::prepareCustomRaffle)
            }.onSuccess(customRaffle::setValue)
                .onFailure(::handleError)
        }
    }

    fun updateItemSelection(index: Int, isSelected: Boolean) {
        customRaffle.value?.run {
            customRaffle.value = apply { items[index].included = isSelected }

            startInBackground { rememberRaffled(RememberRaffled.Params(items[index], isSelected)) }
        }
    }

    fun raffle() {
        validateSelection { showPreferredRaffleMode.setEvent(preferredRaffleMode.value) }
    }

    fun selectMode() {
        validateSelection { showModeSelector.setEvent(Unit) }
    }

    fun itemRaffled(vararg index: Int) {
        if (rememberRaffledItems.value == true) {
            start {
                customRaffle.value?.run {
                    callInBackground {
                        index.forEach {
                            rememberRaffled(RememberRaffled.Params(items[it], included = false))
                        }

                        customRaffleRepository.getCustomRaffleById(id.orZero())
                            .mapCatching(::prepareCustomRaffle)
                    }.onSuccess {
                        customRaffle.value = it
                        itemsRemaining.setEvent(it.includedItems.size)
                    }
                }
            }
        }
    }

    fun hintDismissed() {
        startInBackground { preferencesRepository.setRaffleDetailsHintDismissed() }
    }

    private fun checkForHints() {
        startInBackground {
            if (!preferencesRepository.getRaffleDetailsHintDisplayed()) {
                showHint.postEvent(Unit)
            }
        }
    }

    private fun getCustomRaffleCount() {
        startInBackground {
            customRaffleRepository.getAllCustomRaffles()
                .onSuccess { customRaffleCount.postValue(it.size) }
                .onFailure { customRaffleCount.postValue(1) }
        }
    }

    private suspend fun getPreferences() {
        callInBackground { preferencesRepository.getPreferences() }
            .onSuccess {
                preferredRaffleMode.value = it.preferredRaffleMode
                rouletteMusicEnabled.value = it.rouletteMusicEnabled
                rememberRaffledItems.value = it.rememberRaffledItems
            }
            .onFailure {
                preferredRaffleMode.value = AppConfig.RaffleMode.NONE
                rouletteMusicEnabled.value = false
                rememberRaffledItems.value = false
            }
    }

    private fun prepareCustomRaffle(customRaffle: CustomRaffle): CustomRaffleModel =
        customRaffleModelMapper.map(customRaffle).apply {
            if (rememberRaffledItems.value == false) items.forEach { it.included = true }
        }

    private fun validateSelection(ifValid: () -> Unit) {
        if (customRaffle.value?.itemSelectionIsValid.orFalse()) {
            ifValid()
        } else {
            invalidSelectionError.setEvent(resourceProvider.getString(R.string.custom_raffle_details_mode_invalid_quantity))
        }
    }
}
