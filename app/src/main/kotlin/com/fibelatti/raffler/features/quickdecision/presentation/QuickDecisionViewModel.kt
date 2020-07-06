package com.fibelatti.raffler.features.quickdecision.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.functional.Failure
import com.fibelatti.core.functional.Success
import com.fibelatti.core.functional.error
import com.fibelatti.core.functional.mapCatching
import com.fibelatti.core.functional.value
import com.fibelatti.raffler.core.platform.AppConfig.LOCALE_NONE
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class QuickDecisionViewModel @Inject constructor(
    private val locale: Locale,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val customRaffleRepository: CustomRaffleRepository,
    private val preferencesRepository: PreferencesRepository,
    private val quickDecisionModelMapper: QuickDecisionModelMapper
) : BaseViewModel() {

    val quickDecisionList: LiveData<QuickDecisionList> get() = _quickDecisionList
    private val _quickDecisionList = MutableLiveData<QuickDecisionList>()
    val quickDecisionResult: LiveEvent<QuickDecisionResult> get() = _quickDecisionResult
    private val _quickDecisionResult = MutableLiveEvent<QuickDecisionResult>()
    val showHint: LiveEvent<Unit> get() = _showHint
    private val _showHint = MutableLiveEvent<Unit>()

    init {
        launch {
            if (!preferencesRepository.getQuickDecisionHintDisplayed()) {
                _showHint.postEvent(Unit)
            }
        }
    }

    fun getAllQuickDecisions() {
        launch {
            val quickDecisions = quickDecisionRepository.getAllQuickDecisions()
                .mapCatching { quickDecisions ->
                    quickDecisions.filter { it.locale == locale.language || it.locale == LOCALE_NONE }
                        .let(quickDecisionModelMapper::mapList)
                }
            val customRaffles = customRaffleRepository.getAllCustomRaffles()

            when {
                quickDecisions is Success && customRaffles is Success -> {
                    showQuickDecisions(
                        quickDecisions.value,
                        hasCustomRaffles = customRaffles.value.isNotEmpty()
                    )
                }
                quickDecisions is Success -> showQuickDecisions(quickDecisions.value)
                quickDecisions is Failure -> handleError(quickDecisions.error)
            }
        }
    }

    fun getQuickDecisionResult(quickDecision: QuickDecisionModel, color: Int) {
        _quickDecisionResult.postEvent(
            QuickDecisionResult(quickDecision.description, quickDecision.values.random(), color)
        )
    }

    fun hintDismissed() {
        launch { preferencesRepository.setQuickDecisionHintDismissed() }
    }

    private fun showQuickDecisions(list: List<QuickDecisionModel>, hasCustomRaffles: Boolean = false) {
        _quickDecisionList.postValue(QuickDecisionList(list, hasCustomRaffles))
    }

    data class QuickDecisionList(val quickDecisions: List<QuickDecisionModel>, val hasCustomRaffles: Boolean)
    data class QuickDecisionResult(val title: String, val result: String, val color: Int)
}
