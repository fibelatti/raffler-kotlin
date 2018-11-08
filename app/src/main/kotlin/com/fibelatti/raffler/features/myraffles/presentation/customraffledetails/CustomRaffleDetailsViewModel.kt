package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.extension.orZero
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.functional.mapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.LiveEvent
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

    // region Properties and Backing properties
    val rememberRaffledItems: LiveData<Boolean> get() = _rememberRaffledItems
    private val _rememberRaffledItems by lazy { MutableLiveData<Boolean>() }
    val preferredRaffleMode: LiveData<AppConfig.RaffleMode> get() = _preferredRaffleMode
    private val _preferredRaffleMode by lazy { MutableLiveData<AppConfig.RaffleMode>() }
    val rouletteMusicEnabled: LiveData<Boolean> get() = _rouletteMusicEnabled
    private val _rouletteMusicEnabled by lazy { MutableLiveData<Boolean>() }
    val customRaffle: LiveData<CustomRaffleModel> get() = _customRaffle
    private val _customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }
    val showHint: LiveEvent<Unit> get() = _showHint
    private val _showHint by lazy { MutableLiveEvent<Unit>() }
    val invalidSelectionError: LiveEvent<String> get() = _invalidSelectionError
    private val _invalidSelectionError by lazy { MutableLiveEvent<String>() }
    val showModeSelector: LiveEvent<Unit> get() = _showModeSelector
    private val _showModeSelector by lazy { MutableLiveEvent<Unit>() }
    val showPreferredRaffleMode: LiveEvent<AppConfig.RaffleMode> get() = _showPreferredRaffleMode
    private val _showPreferredRaffleMode by lazy { MutableLiveEvent<AppConfig.RaffleMode>() }
    val itemsRemaining: LiveEvent<Int> get() = _itemsRemaining
    private val _itemsRemaining by lazy { MutableLiveEvent<Int>() }
    val toggleState: LiveEvent<ToggleState> get() = _toggleState
    private val _toggleState by lazy { MutableLiveEvent<ToggleState>() }
    // endregion

    init {
        checkForHints()
    }

    fun getCustomRaffleById(id: Long?) {
        startInBackground {
            preferencesRepository.getPreferences()
                .onSuccess {
                    _preferredRaffleMode.postValue(it.preferredRaffleMode)
                    _rouletteMusicEnabled.postValue(it.rouletteMusicEnabled)
                    _rememberRaffledItems.postValue(it.rememberRaffledItems)
                }
                .onFailure {
                    _preferredRaffleMode.postValue(AppConfig.RaffleMode.NONE)
                    _rouletteMusicEnabled.postValue(false)
                    _rememberRaffledItems.postValue(false)
                }

            customRaffleRepository.getCustomRaffleById(id.orZero())
                .mapCatching(::prepareCustomRaffle)
                .onSuccess { raffle ->
                    _customRaffle.postValue(raffle)
                    _toggleState.postEvent(when {
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
                it.items.getOrNull(index)?.let { item ->
                    rememberRaffled(RememberRaffled.Params(item, isSelected))
                    _customRaffle.postValue(it.apply { items[index].included = isSelected })
                }
            }
        }
    }

    fun raffle() {
        validateSelection { _showPreferredRaffleMode.postEvent(preferredRaffleMode.value) }
    }

    fun selectMode() {
        validateSelection { _showModeSelector.postEvent(Unit) }
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
                                _customRaffle.postValue(it)
                                _itemsRemaining.postEvent(it.includedItems.size)
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
        _customRaffle.postValue(customRaffleModel)
    }

    fun toggleAll() {
        withCustomRaffle { raffle ->
            withToggleState { state ->
                startInBackground {
                    val rememberRaffleResult = raffle.items.map {
                        defer { rememberRaffled(RememberRaffled.Params(it, included = state == ToggleState.INCLUDE_ALL)) }
                    }.awaitAll()

                    if (rememberRaffleResult.all { it is Success }) {
                        _customRaffle.postValue(
                            raffle.apply { items.forEach { it.included = state == ToggleState.INCLUDE_ALL } }
                        )
                        _toggleState.postEvent(
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
                _showHint.postEvent(Unit)
            }
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
            _invalidSelectionError.postEvent(resourceProvider.getString(R.string.custom_raffle_details_mode_invalid_quantity))
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
