package com.fibelatti.raffler.features.quickdecision.presentation

import com.fibelatti.raffler.core.platform.BaseViewType

class AddNewModel : BaseViewType {
    companion object {
        @JvmStatic
        val VIEW_TYPE = AddNewModel::class.hashCode()
    }

    override fun getViewType() = VIEW_TYPE
}
