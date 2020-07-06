package com.fibelatti.raffler.features.myraffles.presentation.common

import com.fibelatti.core.android.base.BaseViewType

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
