package com.fibelatti.raffler.features.quickdecision.presentation

import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.core.functional.TwoWayMapper
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
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

class QuickDecisionModelMapper @Inject constructor() : TwoWayMapper<QuickDecision, QuickDecisionModel> {
    override fun map(param: QuickDecision): QuickDecisionModel = with(param) {
        QuickDecisionModel(id, locale, description, values)
    }

    override fun mapReverse(param: QuickDecisionModel): QuickDecision = with(param) {
        QuickDecision(id, locale, description, values)
    }

    fun map(param: CustomRaffleModel): QuickDecision = with(param) {
        QuickDecision(
            id = id.toString(),
            locale = AppConfig.LOCALE_NONE,
            description = description,
            values = items.map(CustomRaffleItemModel::description)
        )
    }
}
