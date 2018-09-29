package com.fibelatti.raffler.features.quickdecision.data

import androidx.room.Entity
import androidx.room.Ignore
import com.fibelatti.raffler.core.functional.Mapper
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import javax.inject.Inject

const val QUICK_DECISION_DTO_TABLE_NAME = "QuickDecisionDto"
const val QUICK_DECISION_DTO_COLUMN_ID = "id"
const val QUICK_DECISION_DTO_COLUMN_LOCALE = "locale"

@Entity(
    tableName = QUICK_DECISION_DTO_TABLE_NAME,
    primaryKeys = [QUICK_DECISION_DTO_COLUMN_ID, QUICK_DECISION_DTO_COLUMN_LOCALE]
)
data class QuickDecisionDto(
    val id: String,
    val locale: String,
    val description: String,
    val values: List<String>
) {
    @Ignore constructor() : this("", "", "", listOf())
}

class QuickDecisionDtoMapper @Inject constructor() : Mapper<QuickDecisionDto, QuickDecision> {
    override fun map(param: QuickDecisionDto): QuickDecision = with(param) {
        QuickDecision(id, locale, description, values)
    }

    override fun mapReverse(param: QuickDecision): QuickDecisionDto = with(param) {
        QuickDecisionDto(id, locale, description, values)
    }
}
