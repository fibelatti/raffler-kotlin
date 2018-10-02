package com.fibelatti.raffler.core.platform

object AddNewModel : BaseViewType {
    @JvmStatic
    val VIEW_TYPE = AddNewModel::class.hashCode()

    override fun getViewType() = VIEW_TYPE
}
