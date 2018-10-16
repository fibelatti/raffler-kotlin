package com.fibelatti.raffler.features.quickdecision

import com.fibelatti.raffler.core.functional.Result

interface QuickDecisionRepository {
    suspend fun getAllQuickDecisions(): Result<List<QuickDecision>>

    suspend fun getQuickDecisionById(id: String): Result<QuickDecision?>

    suspend fun deleteQuickDecisionById(id: String): Result<Unit>

    suspend fun addQuickDecisions(vararg quickDecision: QuickDecision): Result<Unit>
}
