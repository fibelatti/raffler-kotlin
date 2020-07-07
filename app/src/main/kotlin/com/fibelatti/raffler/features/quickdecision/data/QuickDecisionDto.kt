package com.fibelatti.raffler.features.quickdecision.data

import androidx.annotation.Keep
import androidx.room.Entity
import com.fibelatti.core.functional.TwoWayMapper
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import javax.inject.Inject

const val QUICK_DECISION_DTO_TABLE_NAME = "QuickDecision"
const val QUICK_DECISION_DTO_COLUMN_ID = "id"
const val QUICK_DECISION_DTO_COLUMN_LOCALE = "locale"

@Keep
@Entity(
    tableName = QUICK_DECISION_DTO_TABLE_NAME,
    primaryKeys = [QUICK_DECISION_DTO_COLUMN_ID, QUICK_DECISION_DTO_COLUMN_LOCALE]
)
data class QuickDecisionDto(
    val id: String,
    val locale: String,
    val description: String,
    val values: List<String>
)

class QuickDecisionDtoMapper @Inject constructor() : TwoWayMapper<QuickDecisionDto, QuickDecision> {

    override fun map(param: QuickDecisionDto): QuickDecision = with(param) {
        QuickDecision(id, locale, description, values)
    }

    override fun mapReverse(param: QuickDecision): QuickDecisionDto = with(param) {
        QuickDecisionDto(id, locale, description, values)
    }
}
