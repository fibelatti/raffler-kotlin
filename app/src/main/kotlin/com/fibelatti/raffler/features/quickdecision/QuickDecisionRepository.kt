package com.fibelatti.raffler.features.quickdecision

import com.fibelatti.raffler.core.functional.Either

interface QuickDecisionRepository {
    suspend fun getAllQuickDecisions(): Either<Throwable, List<QuickDecision>>

    suspend fun addQuickDecisions(list: List<QuickDecision>): Either<Throwable, Unit>
}
