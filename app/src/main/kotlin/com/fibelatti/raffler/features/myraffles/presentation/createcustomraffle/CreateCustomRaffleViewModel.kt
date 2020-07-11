package com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.extension.empty
import com.fibelatti.core.functional.isSuccess
import com.fibelatti.core.functional.mapCatching
import com.fibelatti.core.functional.onFailure
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionModelMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateCustomRaffleViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val quickDecisionModelMapper: QuickDecisionModelMapper,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val showEditCustomRaffleLayout: LiveData<CustomRaffleModel> get() = _showEditCustomRaffleLayout
    private val _showEditCustomRaffleLayout = MutableLiveData<CustomRaffleModel>()
    val showCreateCustomRaffleLayout: LiveData<Unit> get() = _showCreateCustomRaffleLayout
    private val _showCreateCustomRaffleLayout = MutableLiveData<Unit>()
    val customRaffle: LiveData<CustomRaffleModel> get() = _customRaffle
    private val _customRaffle = MutableLiveData<CustomRaffleModel>()
    val addAsQuickDecision: LiveData<Boolean> get() = _addAsQuickDecision
    private val _addAsQuickDecision = MutableLiveData<Boolean>()
    val showHint: LiveEvent<Unit> get() = _showHint
    private val _showHint = MutableLiveEvent<Unit>()
    val invalidDescriptionError: LiveData<String> get() = _invalidDescriptionError
    private val _invalidDescriptionError = MutableLiveData<String>()
    val invalidItemsQuantityError: LiveData<String> get() = _invalidItemsQuantityError
    private val _invalidItemsQuantityError = MutableLiveData<String>()
    val invalidItemDescriptionError: LiveData<String> get() = _invalidItemDescriptionError
    private val _invalidItemDescriptionError = MutableLiveData<String>()
    val invalidEditError: LiveData<String> get() = _invalidEditError
    private val _invalidEditError = MutableLiveData<String>()
    val onChangedSaved: LiveEvent<CustomRaffleModel> get() = _onChangedSaved
    private val _onChangedSaved = MutableLiveEvent<CustomRaffleModel>()
    val onDeleted: LiveEvent<Unit> get() = _onDeleted
    private val _onDeleted = MutableLiveEvent<Unit>()

    fun getCustomRaffleById(id: Long?, addAsShortcut: Boolean?) {
        if (id != null && id != 0L) {
            launch {
                customRaffleRepository.getCustomRaffleById(id)
                    .mapCatching(customRaffleModelMapper::map)
                    .onSuccess { customRaffleModel ->
                        _customRaffle.postValue(customRaffleModel)
                        _showEditCustomRaffleLayout.postValue(customRaffleModel)

                        quickDecisionRepository.getQuickDecisionById(customRaffleModel.id.toString())
                            .onSuccess { quickDecision -> _addAsQuickDecision.postValue(quickDecision != null) }
                    }
                    .onFailure(::handleError)
            }
        } else {
            if (addAsShortcut == true) {
                launch { checkForHints() }
            }

            _showCreateCustomRaffleLayout.postValue(Unit)
            _customRaffle.postValue(CustomRaffleModel.empty())
        }
    }

    fun setDescription(newDescription: String) {
        val raffle = customRaffle.value ?: CustomRaffleModel.empty()
        _customRaffle.postValue(raffle.copy(description = newDescription))
    }

    fun addItem(newItemDescription: String) {
        when {
            newItemDescription.isBlank() -> {
                _invalidItemDescriptionError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_create_invalid_description)
                )
            }
            customRaffle.value?.items?.find { it.description == newItemDescription } != null -> {
                _invalidItemDescriptionError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_create_duplicate)
                )
            }
            else -> {
                val newItem = CustomRaffleItemModel.empty().copy(description = newItemDescription)
                val raffle = customRaffle.value ?: CustomRaffleModel.empty()

                _customRaffle.postValue(raffle.copy(items = raffle.items + newItem))
                _invalidItemDescriptionError.postValue(String.empty())
            }
        }
    }

    fun editItem(itemPosition: Int, newItemDescription: String) {
        when {
            newItemDescription.isBlank() -> return
            customRaffle.value?.items?.find { it.description == newItemDescription } != null -> {
                _invalidEditError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_create_duplicate)
                )
            }
            else -> {
                val raffle = customRaffle.value ?: return
                val updatedRaffle = raffle.copy(
                    items = raffle.items.mapIndexed { index, customRaffleItemModel ->
                        if (index == itemPosition) {
                            customRaffleItemModel.copy(description = newItemDescription)
                        } else {
                            customRaffleItemModel
                        }
                    }
                )

                _customRaffle.postValue(updatedRaffle)
            }
        }
    }

    fun removeItem(position: Int) {
        val currentRaffle = customRaffle.value ?: return
        _customRaffle.postValue(currentRaffle.copy(items = currentRaffle.items - currentRaffle.items[position]))
    }

    fun removeAllItems() {
        val currentRaffle = customRaffle.value ?: return
        _customRaffle.postValue(currentRaffle.copy(items = listOf()))
    }

    fun save(saveAsQuickDecision: Boolean) {
        launch {
            validateData { customRaffle ->
                customRaffleRepository.saveCustomRaffle(
                    customRaffleModelMapper.mapReverse(
                        customRaffle
                    )
                )
                    .mapCatching(customRaffleModelMapper::map)
                    .onSuccess {
                        if (saveAsQuickDecision) {
                            quickDecisionRepository.addQuickDecisions(
                                quickDecisionModelMapper.map(it)
                            )
                        } else {
                            quickDecisionRepository.deleteQuickDecisionById(it.id.toString())
                        }

                        _onChangedSaved.postEvent(it)
                    }
                    .onFailure {
                        handleError(Throwable(resourceProvider.getString(R.string.generic_msg_error)))
                    }
            }
        }
    }

    fun delete() {
        val currentRaffle = customRaffle.value ?: return

        launch {
            val resultCustomRaffle = async {
                customRaffleRepository.deleteCustomRaffleById(currentRaffle.id)
            }

            val resultQuickDecision = async {
                quickDecisionRepository.deleteQuickDecisionById(currentRaffle.id.toString())
            }

            if (resultCustomRaffle.await().isSuccess && resultQuickDecision.await().isSuccess) {
                _onDeleted.postEvent(Unit)
            } else {
                handleError(Throwable(resourceProvider.getString(R.string.generic_msg_error)))
            }
        }
    }

    fun hintDismissed() {
        launch { preferencesRepository.setAddNewQuickDecisionDismissed() }
    }

    private suspend fun checkForHints() {
        launch {
            if (!preferencesRepository.getAddNewQuickDecisionDisplayed()) {
                _showHint.postEvent(Unit)
            }
        }
    }

    private inline fun validateData(ifValid: (CustomRaffleModel) -> Unit) {
        val currentRaffle = customRaffle.value ?: return

        when {
            currentRaffle.description.isBlank() -> {
                _invalidDescriptionError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_create_invalid_description)
                )
            }
            currentRaffle.items.size < 2 -> {
                _invalidItemsQuantityError.postValue(
                    resourceProvider.getString(R.string.custom_raffle_create_invalid_items_quantity)
                )
            }
            else -> {
                _invalidDescriptionError.postValue(String.empty())
                _invalidItemsQuantityError.postValue(String.empty())

                ifValid(currentRaffle)
            }
        }
    }
}
