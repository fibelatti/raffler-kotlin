package com.fibelatti.raffler.features.myraffles.presentation

import com.fibelatti.raffler.core.platform.BaseViewType

data class CustomRaffleDraftedModel(
    val title: String,
    val description: String
) : BaseViewType {
    companion object {
        @JvmStatic
        val VIEW_TYPE = CustomRaffleDraftedModel::class.hashCode()
    }

    override fun getViewType(): Int = VIEW_TYPE
}
