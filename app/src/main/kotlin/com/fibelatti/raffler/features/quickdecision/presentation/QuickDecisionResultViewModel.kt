package com.fibelatti.raffler.features.quickdecision.presentation

import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import javax.inject.Inject

class QuickDecisionResultViewModel @Inject constructor(): BaseViewModel() {

    val quickDecisionResult: LiveEvent<QuickDecisionResult> get() = _quickDecisionResult
    private val _quickDecisionResult = MutableLiveEvent<QuickDecisionResult>()

    fun getQuickDecisionResult(quickDecision: QuickDecisionModel) {
        _quickDecisionResult.postEvent(
            QuickDecisionResult(
                quickDecision.description,
                quickDecision.values.random()
            )
        )
    }

    data class QuickDecisionResult(val title: String, val result: String)
}
