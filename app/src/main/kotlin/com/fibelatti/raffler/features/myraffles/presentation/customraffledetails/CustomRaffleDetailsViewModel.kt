package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.extension.orZero
import com.fibelatti.core.functional.Success
import com.fibelatti.core.functional.mapCatching
import com.fibelatti.core.functional.onFailure
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.RememberRaffled
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomRaffleDetailsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val rememberRaffled: RememberRaffled,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val rememberRaffledItems: LiveData<Boolean> get() = _rememberRaffledItems
    private val _rememberRaffledItems = MutableLiveData<Boolean>()
    val preferredRaffleMode: LiveData<AppConfig.RaffleMode> get() = _preferredRaffleMode
    private val _preferredRaffleMode = MutableLiveData<AppConfig.RaffleMode>()
    val rouletteMusicEnabled: LiveData<Boolean> get() = _rouletteMusicEnabled
    private val _rouletteMusicEnabled = MutableLiveData<Boolean>()
    val customRaffle: LiveData<CustomRaffleModel> get() = _customRaffle
    private val _customRaffle = MutableLiveData<CustomRaffleModel>()
    val showHint: LiveEvent<Unit> get() = _showHint
    private val _showHint = MutableLiveEvent<Unit>()
    val invalidSelectionError: LiveEvent<String> get() = _invalidSelectionError
    private val _invalidSelectionError = MutableLiveEvent<String>()
    val showModeSelector: LiveEvent<Unit> get() = _showModeSelector
    private val _showModeSelector = MutableLiveEvent<Unit>()
    val showPreferredRaffleMode: LiveEvent<AppConfig.RaffleMode> get() = _showPreferredRaffleMode
    private val _showPreferredRaffleMode = MutableLiveEvent<AppConfig.RaffleMode>()
    val itemsRemaining: LiveEvent<Int> get() = _itemsRemaining
    private val _itemsRemaining = MutableLiveEvent<Int>()
    val toggleState: LiveEvent<ToggleState> get() = _toggleState
    private val _toggleState = MutableLiveEvent<ToggleState>()

    init {
        checkForHints()
    }

    fun getCustomRaffleById(id: Long?) {
        launch {
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
        val currentRaffle = customRaffle.value ?: return
        launch {
            currentRaffle.items.getOrNull(index)?.let { item ->
                rememberRaffled(RememberRaffled.Params(item, isSelected))

                currentRaffle.copy(
                    items = currentRaffle.items.mapIndexed { currentIndex, currentItem ->
                        if (currentIndex == index) {
                            currentItem.copy(included = isSelected)
                        } else {
                            currentItem
                        }
                    }
                ).let(_customRaffle::postValue)
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
        if (rememberRaffledItems.value != true) {
            return
        }

        val currentRaffle = customRaffle.value ?: return

        launch {
            val rememberRaffleResult = currentRaffle.items
                .filterIndexed { idx, _ -> idx in index }
                .map { item ->
                    async {
                        rememberRaffled(RememberRaffled.Params(item, included = false))
                    }
                }
                .awaitAll()

            if (rememberRaffleResult.isNotEmpty() && rememberRaffleResult.all { it is Success }) {
                customRaffleRepository.getCustomRaffleById(currentRaffle.id.orZero())
                    .mapCatching(::prepareCustomRaffle)
                    .onSuccess {
                        _customRaffle.postValue(it)
                        _itemsRemaining.postEvent(it.includedItems.size)
                    }
            }
        }
    }

    fun hintDismissed() {
        launch { preferencesRepository.setRaffleDetailsHintDismissed() }
    }

    fun setCustomRaffleForContinuation(customRaffleModel: CustomRaffleModel) {
        _customRaffle.postValue(customRaffleModel)
    }

    fun toggleAll() {
        val currentRaffle = customRaffle.value ?: return
        val currentState = toggleState.value?.peekContent() ?: return

        launch {
            val rememberRaffleResult = currentRaffle.items
                .map {
                    async {
                        rememberRaffled(
                            RememberRaffled.Params(
                                it,
                                included = currentState == ToggleState.INCLUDE_ALL
                            )
                        )
                    }
                }
                .awaitAll()

            if (rememberRaffleResult.all { it is Success }) {
                currentRaffle.copy(
                    items = currentRaffle.items.map {
                        it.copy(included = currentState == ToggleState.INCLUDE_ALL)
                    }
                ).let(_customRaffle::postValue)

                _toggleState.postEvent(
                    when (currentState) {
                        ToggleState.INCLUDE_ALL -> ToggleState.EXCLUDE_ALL
                        ToggleState.EXCLUDE_ALL -> ToggleState.INCLUDE_ALL
                    }
                )
            }
        }
    }

    private fun checkForHints() {
        launch {
            if (!preferencesRepository.getRaffleDetailsHintDisplayed()) {
                _showHint.postEvent(Unit)
            }
        }
    }

    private fun prepareCustomRaffle(customRaffle: CustomRaffle): CustomRaffleModel =
        customRaffleModelMapper.map(customRaffle).let { model ->
            if (rememberRaffledItems.value == false) {
                model.copy(items = model.items.map { it.copy(included = true) })
            } else {
                model
            }
        }

    private fun validateSelection(ifValid: () -> Unit) {
        if (customRaffle.value?.itemSelectionIsValid == true) {
            ifValid()
        } else {
            _invalidSelectionError.postEvent(
                resourceProvider.getString(R.string.custom_raffle_details_mode_invalid_quantity)
            )
        }
    }

    enum class ToggleState {
        INCLUDE_ALL,
        EXCLUDE_ALL,
    }
}
