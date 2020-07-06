package com.fibelatti.raffler.core.platform.recyclerview

import com.fibelatti.core.android.base.BaseViewType

object AddNewModel : BaseViewType {

    @JvmStatic
    val VIEW_TYPE = AddNewModel::class.hashCode()

    override fun getViewType() = VIEW_TYPE
}
