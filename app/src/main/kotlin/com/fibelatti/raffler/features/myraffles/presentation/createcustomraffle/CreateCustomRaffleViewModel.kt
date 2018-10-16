package com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.functional.isSuccess
import com.fibelatti.raffler.core.functional.mapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
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
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val showEditCustomRaffleLayout by lazy { MutableLiveData<Unit>() }
    val showCreateCustomRaffleLayout by lazy { MutableLiveData<Unit>() }
    val customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }
    val addAsQuickDecision by lazy { MutableLiveData<Boolean>() }
    val showHint by lazy { MutableLiveEvent<Unit>() }
    val invalidDescriptionError by lazy { MutableLiveData<String>() }
    val invalidItemsQuantityError by lazy { MutableLiveData<String>() }
    val invalidItemDescriptionError by lazy { MutableLiveData<String>() }
    val onChangedSaved by lazy { MutableLiveData<Boolean>() }
    val onDeleted by lazy { MutableLiveData<Boolean>() }

    fun getCustomRaffleById(id: Long?) {
        if (id != null && id != 0L) {
            start {
                inBackground {
                    customRaffleRepository.getCustomRaffleById(id)
                        .mapCatching(customRaffleModelMapper::map)
                }.onSuccess {
                    customRaffle.value = it
                    showEditCustomRaffleLayout.value = Unit
                }.onFailure(::handleError)

                customRaffle.value?.let { customRaffle ->
                    inBackground {
                        quickDecisionRepository.getQuickDecisionById(customRaffle.description)
                    }.onSuccess {
                        addAsQuickDecision.value = it != null
                        checkForHints()
                    }.onFailure(::handleError)
                }
            }
        } else {
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
        if (newItemDescription.isNotBlank()) {
            val newItem = CustomRaffleItemModel.empty().copy(description = newItemDescription)

            customRaffle.value = customRaffle.value?.apply {
                items.add(0, newItem)
            } ?: CustomRaffleModel.empty().apply {
                items.add(0, newItem)
            }
            invalidItemDescriptionError.value = String.empty()
        } else {
            invalidItemDescriptionError.value = resourceProvider.getString(R.string.custom_raffle_create_invalid_description)
        }
    }

    fun removeItem(position: Int) {
        customRaffle.value = customRaffle.value?.apply { items.removeAt(position) }
    }

    fun removeAllItems() {
        customRaffle.value = customRaffle.value?.apply { items.clear() }
    }

    fun save(saveAsQuickDecision: Boolean) {
        validateData {
            start {
                val resultCustomRaffle = inBackgroundForParallel {
                    customRaffleRepository.saveCustomRaffle(customRaffleModelMapper.mapReverse(it))
                }
                val resultQuickDecision = inBackgroundForParallel {
                    if (saveAsQuickDecision) {
                        quickDecisionRepository.addQuickDecisions(customRaffleToQuickDecisionMapper.map(it))
                    } else {
                        quickDecisionRepository.deleteQuickDecisionById(it.description)
                    }
                }

                if (resultCustomRaffle.await().isSuccess && resultQuickDecision.await().isSuccess) {
                    onChangedSaved.value = true
                } else {
                    error.value = Throwable(resourceProvider.getString(R.string.generic_msg_error))
                }
            }
        }
    }

    fun delete() {
        customRaffle.value?.let { customRaffle ->
            start {
                val resultCustomRaffle = inBackgroundForParallel {
                    customRaffleRepository.deleteCustomRaffleById(customRaffle.id)
                }

                val resultQuickDecision = inBackgroundForParallel {
                    quickDecisionRepository.deleteQuickDecisionById(customRaffle.description)
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

    private fun checkForHints() {
        startInBackground {
            if (!preferencesRepository.getAddNewQuickDecisionDisplayed()) {
                showHint.postEvent(Unit)
            }
        }
    }

    private fun validateData(ifValid: (CustomRaffleModel) -> Unit) {
        customRaffle.value?.let {
            when {
                it.description.isBlank() -> {
                    invalidDescriptionError.value = resourceProvider.getString(R.string.custom_raffle_create_invalid_description)
                }
                it.items.size < 2 -> {
                    invalidItemsQuantityError.value = resourceProvider.getString(R.string.custom_raffle_create_invalid_items_quantity)
                }
                else -> {
                    invalidDescriptionError.value = String.empty()
                    invalidItemsQuantityError.value = String.empty()

                    ifValid(it)
                }
            }
        }
    }
}
