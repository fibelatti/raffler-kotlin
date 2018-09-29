package com.fibelatti.raffler.features.quickdecision.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.extension.random
import com.fibelatti.raffler.core.functional.Either
import com.fibelatti.raffler.core.functional.flatMap
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import kotlinx.coroutines.experimental.launch
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
        launch {
            inBackground {
                quickDecisionRepository.getAllQuickDecisions()
                    .flatMap { Either.right(it.filterByLocale()) }
            }.either(::handleError, ::showQuickDecisions)
        }
    }

    fun getQuickDecisionResult(quickDecision: QuickDecisionModel, color: Int) {
        state.value = State.ShowResult(quickDecision.description, quickDecision.values.random(), color)
    }

    private fun List<QuickDecision>.filterByLocale(): List<QuickDecisionModel> {
        val currentLocale = if (AppConfig.supportedLocales.contains(locale.language.toLowerCase())) {
            locale.language
        } else {
            AppConfig.LOCALE_EN
        }

        return filter { it.locale == currentLocale || it.locale == AppConfig.LOCALE_NONE }
            .map(quickDecisionModelMapper::map)
    }

    private fun showQuickDecisions(list: List<QuickDecisionModel>) {
        state.value = State.ShowList(list)
    }

    sealed class State {
        data class ShowList(val quickDecisions: List<QuickDecisionModel>) : State()
        data class ShowResult(val title: String, val result: String, val color: Int) : State()
    }
}
