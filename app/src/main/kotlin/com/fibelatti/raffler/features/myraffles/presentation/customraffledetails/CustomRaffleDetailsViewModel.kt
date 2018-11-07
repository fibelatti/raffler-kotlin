package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.extension.orZero
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.functional.mapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.RememberRaffled
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import kotlinx.coroutines.awaitAll
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
    val toggleState by lazy { MutableLiveEvent<ToggleState>() }

    init {
        checkForHints()
        getCustomRaffleCount()
    }

    fun getCustomRaffleById(id: Long?) {
        startInBackground {
            preferencesRepository.getPreferences()
                .onSuccess {
                    preferredRaffleMode.postValue(it.preferredRaffleMode)
                    rouletteMusicEnabled.postValue(it.rouletteMusicEnabled)
                    rememberRaffledItems.postValue(it.rememberRaffledItems)
                }
                .onFailure {
                    preferredRaffleMode.postValue(AppConfig.RaffleMode.NONE)
                    rouletteMusicEnabled.postValue(false)
                    rememberRaffledItems.postValue(false)
                }

            customRaffleRepository.getCustomRaffleById(id.orZero())
                .mapCatching(::prepareCustomRaffle)
                .onSuccess { raffle ->
                    customRaffle.postValue(raffle)
                    toggleState.postEvent(when {
                        raffle.items.any { !it.included } -> ToggleState.INCLUDE_ALL
                        raffle.items.all { !it.included } -> ToggleState.INCLUDE_ALL
                        else -> ToggleState.EXCLUDE_ALL
                    })
                }
                .onFailure(::handleError)
        }
    }

    fun updateItemSelection(index: Int, isSelected: Boolean) {
        withCustomRaffle {
            startInBackground {
                rememberRaffled(RememberRaffled.Params(it.items[index], isSelected))
                customRaffle.postValue(it.apply { it.items[index].included = isSelected })
            }
        }
    }

    fun raffle() {
        validateSelection { showPreferredRaffleMode.postEvent(preferredRaffleMode.value) }
    }

    fun selectMode() {
        validateSelection { showModeSelector.postEvent(Unit) }
    }

    fun itemRaffled(vararg index: Int) {
        if (rememberRaffledItems.value == true) {
            withCustomRaffle { raffle ->
                startInBackground {
                    val rememberRaffleResult = index.map {
                        defer { rememberRaffled(RememberRaffled.Params(raffle.items[it], included = false)) }
                    }.awaitAll()

                    if (rememberRaffleResult.all { it is Success }) {
                        customRaffleRepository.getCustomRaffleById(raffle.id.orZero())
                            .mapCatching(::prepareCustomRaffle)
                            .onSuccess {
                                customRaffle.postValue(it)
                                itemsRemaining.postEvent(it.includedItems.size)
                            }
                    }
                }
            }
        }
    }

    fun hintDismissed() {
        startInBackground { preferencesRepository.setRaffleDetailsHintDismissed() }
    }

    fun setCustomRaffleForContinuation(customRaffleModel: CustomRaffleModel) {
        customRaffle.postValue(customRaffleModel)
    }

    fun toggleAll() {
        withCustomRaffle { raffle ->
            withToggleState { state ->
                startInBackground {
                    val rememberRaffleResult = raffle.items.map {
                        defer { rememberRaffled(RememberRaffled.Params(it, included = state == ToggleState.INCLUDE_ALL)) }
                    }.awaitAll()

                    if (rememberRaffleResult.all { it is Success }) {
                        customRaffle.postValue(
                            raffle.apply { items.forEach { it.included = state == ToggleState.INCLUDE_ALL } }
                        )
                        toggleState.postEvent(
                            when (state) {
                                ToggleState.INCLUDE_ALL -> ToggleState.EXCLUDE_ALL
                                ToggleState.EXCLUDE_ALL -> ToggleState.INCLUDE_ALL
                            }
                        )
                    }
                }
            }
        }
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

    private fun prepareCustomRaffle(customRaffle: CustomRaffle): CustomRaffleModel =
        customRaffleModelMapper.map(customRaffle).apply {
            if (rememberRaffledItems.value == false) items.forEach { it.included = true }
        }

    private fun validateSelection(ifValid: () -> Unit) {
        if (customRaffle.value?.itemSelectionIsValid.orFalse()) {
            ifValid()
        } else {
            invalidSelectionError.postEvent(resourceProvider.getString(R.string.custom_raffle_details_mode_invalid_quantity))
        }
    }

    private fun withCustomRaffle(body: (CustomRaffleModel) -> Unit) {
        customRaffle.value?.let(body)
    }

    private fun withToggleState(body: (ToggleState) -> Unit) {
        toggleState.value?.peekContent()?.let(body)
    }

    enum class ToggleState {
        INCLUDE_ALL, EXCLUDE_ALL
    }
}
