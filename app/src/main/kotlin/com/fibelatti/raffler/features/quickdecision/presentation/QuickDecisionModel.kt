package com.fibelatti.raffler.features.quickdecision.presentation

import com.fibelatti.raffler.core.functional.Mapper
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.BaseViewType
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import javax.inject.Inject

data class QuickDecisionModel(
    val id: String,
    val locale: String,
    val description: String,
    val values: List<String>
) : BaseViewType {
    companion object {
        @JvmStatic
        val VIEW_TYPE = QuickDecisionModel::class.hashCode()
    }

    override fun getViewType(): Int = VIEW_TYPE
}

class QuickDecisionModelMapper @Inject constructor() : Mapper<QuickDecision, QuickDecisionModel> {
    override fun map(param: QuickDecision): QuickDecisionModel = with(param) {
        QuickDecisionModel(id, locale, description, values)
    }

    override fun mapReverse(param: QuickDecisionModel): QuickDecision = with(param) {
        QuickDecision(id, locale, description, values)
    }
}

class CustomRaffleToQuickDecisionMapper @Inject constructor() : Mapper<CustomRaffleModel, QuickDecision> {
    override fun map(param: CustomRaffleModel): QuickDecision = with(param) {
        QuickDecision(
            id = description,
            locale = AppConfig.LOCALE_NONE,
            description = description,
            values = items.map { it.description }
        )
    }

    override fun mapReverse(param: QuickDecision): CustomRaffleModel {
        throw RuntimeException("Invalid mapReverse call.")
    }
}
