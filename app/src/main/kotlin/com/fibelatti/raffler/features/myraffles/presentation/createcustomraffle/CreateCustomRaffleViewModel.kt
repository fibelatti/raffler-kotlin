package com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.functional.isSuccess
import com.fibelatti.raffler.core.functional.mapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.fibelatti.raffler.features.quickdecision.presentation.CustomRaffleToQuickDecisionMapper
import javax.inject.Inject

class CreateCustomRaffleViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val customRaffleToQuickDecisionMapper: CustomRaffleToQuickDecisionMapper,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val showEditCustomRaffleLayout by lazy { MutableLiveData<Unit>() }
    val showCreateCustomRaffleLayout by lazy { MutableLiveData<Unit>() }
    val customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }
    val addAsQuickDecision by lazy { MutableLiveData<Boolean>() }
    val showHint by lazy { MutableLiveEvent<Unit>() }
    val invalidDescriptionError by lazy { MutableLiveData<String>() }
    val invalidItemsQuantityError by lazy { MutableLiveData<String>() }
    val invalidItemDescriptionError by lazy { MutableLiveData<String>() }
    val onChangedSaved by lazy { MutableLiveData<CustomRaffleModel>() }
    val onDeleted by lazy { MutableLiveData<Boolean>() }

    fun getCustomRaffleById(id: Long?, addAsShortcut: Boolean?) {
        if (id != null && id != 0L) {
            startInBackground {
                customRaffleRepository.getCustomRaffleById(id)
                    .mapCatching(customRaffleModelMapper::map)
                    .onSuccess { customRaffleModel ->
                        customRaffle.postValue(customRaffleModel)
                        showEditCustomRaffleLayout.postValue(Unit)

                        quickDecisionRepository.getQuickDecisionById(customRaffleModel.id.toString())
                            .onSuccess { quickDecision -> addAsQuickDecision.postValue(quickDecision != null) }
                    }
                    .onFailure(::handleError)
            }
        } else {
            if (addAsShortcut.orFalse()) startInBackground { checkForHints() }

            showCreateCustomRaffleLayout.value = Unit
            customRaffle.value = CustomRaffleModel.empty()
        }
    }

    fun setDescription(newDescription: String) {
        customRaffle.value = customRaffle.value
            ?.copy(description = newDescription)
            ?: CustomRaffleModel.empty().copy(description = newDescription)
    }

    fun addItem(newItemDescription: String) {
        when {
            newItemDescription.isBlank() -> {
                invalidItemDescriptionError.value = resourceProvider.getString(R.string.custom_raffle_create_invalid_description)
            }
            customRaffle.value?.items?.find { it.description == newItemDescription } != null -> {

            }
            else -> {
                val newItem = CustomRaffleItemModel.empty().copy(description = newItemDescription)

                customRaffle.value = customRaffle.value?.apply { items.add(0, newItem) }
                    ?: CustomRaffleModel.empty().apply { items.add(0, newItem) }
                invalidItemDescriptionError.value = String.empty()
            }
        }
    }

    fun removeItem(position: Int) {
        customRaffle.value = customRaffle.value?.apply { items.removeAt(position) }
    }

    fun removeAllItems() {
        customRaffle.value = customRaffle.value?.apply { items.clear() }
    }

    fun save(saveAsQuickDecision: Boolean) {
        startInBackground {
            validateData { customRaffle ->
                customRaffleRepository.saveCustomRaffle(customRaffleModelMapper.mapReverse(customRaffle))
                    .mapCatching(customRaffleModelMapper::map)
                    .onSuccess {
                        if (saveAsQuickDecision) {
                            quickDecisionRepository.addQuickDecisions(customRaffleToQuickDecisionMapper.map(it))
                        } else {
                            quickDecisionRepository.deleteQuickDecisionById(it.id.toString())
                        }

                        onChangedSaved.postValue(it)
                    }
                    .onFailure {
                        error.postValue(Throwable(resourceProvider.getString(R.string.generic_msg_error)))
                    }
            }
        }
    }

    fun delete() {
        customRaffle.value?.let { customRaffle ->
            start {
                val resultCustomRaffle = defer {
                    customRaffleRepository.deleteCustomRaffleById(customRaffle.id)
                }

                val resultQuickDecision = defer {
                    quickDecisionRepository.deleteQuickDecisionById(customRaffle.id.toString())
                }

                if (resultCustomRaffle.await().isSuccess && resultQuickDecision.await().isSuccess) {
                    onDeleted.value = true
                } else {
                    error.value = Throwable(resourceProvider.getString(R.string.generic_msg_error))
                }
            }
        }
    }

    fun hintDismissed() {
        startInBackground { preferencesRepository.setAddNewQuickDecisionDismissed() }
    }

    private suspend fun checkForHints() {
        callInBackground {
            if (!preferencesRepository.getAddNewQuickDecisionDisplayed()) {
                showHint.postEvent(Unit)
            }
        }
    }

    private inline fun validateData(ifValid: (CustomRaffleModel) -> Unit) {
        customRaffle.value?.let {
            when {
                it.description.isBlank() -> {
                    invalidDescriptionError.postValue(resourceProvider.getString(R.string.custom_raffle_create_invalid_description))
                }
                it.items.size < 2 -> {
                    invalidItemsQuantityError.postValue(resourceProvider.getString(R.string.custom_raffle_create_invalid_items_quantity))
                }
                else -> {
                    invalidDescriptionError.postValue(String.empty())
                    invalidItemsQuantityError.postValue(String.empty())

                    ifValid(it)
                }
            }
        }
    }
}
