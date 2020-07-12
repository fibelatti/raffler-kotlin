package com.fibelatti.raffler.features.myraffles.presentation.list

import com.fibelatti.core.android.base.BaseAdapterWithDelegates
import com.fibelatti.raffler.core.platform.recyclerview.AddNewDelegateAdapter
import com.fibelatti.raffler.core.platform.recyclerview.AddNewModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import javax.inject.Inject

class CustomRaffleAdapter @Inject constructor(
    private val customRaffleDelegateAdapter: CustomRaffleDelegateAdapter,
    private val addNewDelegateAdapter: AddNewDelegateAdapter
) : BaseAdapterWithDelegates() {

    init {
        delegateAdapters.apply {
            put(AddNewModel.VIEW_TYPE, addNewDelegateAdapter)
            put(CustomRaffleModel.VIEW_TYPE, customRaffleDelegateAdapter)
        }
    }

    var customRaffleClickListener: (Long) -> Unit = { }
        set(value) {
            field = value
            customRaffleDelegateAdapter.clickListener = value
        }
    var addNewClickListener: () -> Unit = {}
        set(value) {
            field = value
            addNewDelegateAdapter.clickListener = value
        }
    var colorList: List<Int> = ArrayList()
        set(value) {
            field = value
            customRaffleDelegateAdapter.colorList = value
            addNewDelegateAdapter.colorList = value
        }
}
