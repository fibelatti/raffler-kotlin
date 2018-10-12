package com.fibelatti.raffler.features.quickdecision.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.extension.random
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.AppConfig.LOCALE_NONE
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import java.util.Locale
import javax.inject.Inject

class QuickDecisionViewModel @Inject constructor(
    private val locale: Locale,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val quickDecisionModelMapper: QuickDecisionModelMapper,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val state by lazy { MutableLiveData<State>() }

    fun getAllQuickDecisions() {
        startInBackground {
            quickDecisionRepository.getAllQuickDecisions()
                .flatMapCatching { it.filterByLocale() }
                .onSuccess(::showQuickDecisions)
                .onFailure(::handleError)
        }
    }

    fun getQuickDecisionResult(quickDecision: QuickDecisionModel, color: Int) {
        state.postValue(State.ShowResult(quickDecision.description, quickDecision.values.random(), color))
    }

    private fun List<QuickDecision>.filterByLocale(): List<QuickDecisionModel> =
        filter { it.locale == locale.language || it.locale == LOCALE_NONE }.map(quickDecisionModelMapper::map)

    private fun showQuickDecisions(list: List<QuickDecisionModel>) {
        state.postValue(State.ShowList(list))
    }

    sealed class State {
        data class ShowList(val quickDecisions: List<QuickDecisionModel>) : State()
        data class ShowResult(val title: String, val result: String, val color: Int) : State()
    }
}
