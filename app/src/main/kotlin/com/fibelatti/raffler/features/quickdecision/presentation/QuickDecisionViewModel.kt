package com.fibelatti.raffler.features.quickdecision.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.extension.random
import com.fibelatti.raffler.core.functional.Failure
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.functional.error
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.functional.value
import com.fibelatti.raffler.core.platform.AppConfig.LOCALE_NONE
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import java.util.Locale
import javax.inject.Inject

class QuickDecisionViewModel @Inject constructor(
    private val locale: Locale,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val customRaffleRepository: CustomRaffleRepository,
    private val preferencesRepository: PreferencesRepository,
    private val quickDecisionModelMapper: QuickDecisionModelMapper,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val state by lazy { MutableLiveData<State>() }
    val showHint by lazy { MutableLiveEvent<Unit>() }

    init {
        checkForHints()
    }

    fun getAllQuickDecisions() {
        start {
            val quickDecisions = inBackground {
                quickDecisionRepository.getAllQuickDecisions()
                    .flatMapCatching { it.filterByLocale() }
            }
            val customRaffles = inBackground { customRaffleRepository.getAllCustomRaffles() }

            when {
                quickDecisions is Success && customRaffles is Success -> {
                    showQuickDecisions(quickDecisions.value, hasCustomRaffles = customRaffles.value.isNotEmpty())
                }
                quickDecisions is Success -> {
                    showQuickDecisions(quickDecisions.value)
                }
                quickDecisions is Failure -> {
                    handleError(quickDecisions.error)
                }
            }
        }
    }

    fun getQuickDecisionResult(quickDecision: QuickDecisionModel, color: Int) {
        state.postValue(State.ShowResult(quickDecision.description, quickDecision.values.random(), color))
    }

    fun hintDismissed() {
        startInBackground { preferencesRepository.setQuickDecisionHintDismissed() }
    }

    private fun checkForHints() {
        startInBackground {
            if (!preferencesRepository.getQuickDecisionHintDisplayed()) {
                showHint.postEvent(Unit)
            }
        }
    }

    private fun List<QuickDecision>.filterByLocale(): List<QuickDecisionModel> =
        filter { it.locale == locale.language || it.locale == LOCALE_NONE }.map(quickDecisionModelMapper::map)

    private fun showQuickDecisions(list: List<QuickDecisionModel>, hasCustomRaffles: Boolean = false) {
        state.postValue(State.ShowList(list, hasCustomRaffles))
    }

    sealed class State {
        data class ShowList(val quickDecisions: List<QuickDecisionModel>, val hasCustomRaffles: Boolean) : State()
        data class ShowResult(val title: String, val result: String, val color: Int) : State()
    }
}
