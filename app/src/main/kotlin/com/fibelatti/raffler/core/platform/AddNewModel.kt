package com.fibelatti.raffler.core.platform

class AddNewModel : BaseViewType {
    companion object {
        @JvmStatic
        val VIEW_TYPE = AddNewModel::class.hashCode()
    }

    override fun getViewType() = VIEW_TYPE
}
