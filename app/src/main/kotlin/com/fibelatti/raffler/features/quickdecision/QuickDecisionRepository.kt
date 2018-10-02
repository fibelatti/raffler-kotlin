package com.fibelatti.raffler.features.quickdecision

import com.fibelatti.raffler.core.functional.Result

interface QuickDecisionRepository {
    suspend fun getAllQuickDecisions(): Result<List<QuickDecision>>

    suspend fun addQuickDecisions(list: List<QuickDecision>): Result<Unit>
}
