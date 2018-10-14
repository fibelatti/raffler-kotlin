package com.fibelatti.raffler.features.quickdecision.presentation.adapter

import android.view.View
import androidx.collection.SparseArrayCompat
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import com.fibelatti.raffler.core.platform.base.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.recyclerview.AddNewDelegateAdapter
import com.fibelatti.raffler.core.platform.recyclerview.AddNewModel
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionModel
import javax.inject.Inject

class QuickDecisionAdapter @Inject constructor(
    private val quickDecisionDelegateAdapter: QuickDecisionDelegateAdapter,
    private val addNewDelegateAdapter: AddNewDelegateAdapter
) : BaseAdapter() {
    override val delegateAdapters: SparseArrayCompat<BaseDelegateAdapter> by lazy {
        SparseArrayCompat<BaseDelegateAdapter>().apply {
            put(AddNewModel.VIEW_TYPE, addNewDelegateAdapter)
            put(QuickDecisionModel.VIEW_TYPE, quickDecisionDelegateAdapter)
        }
    }

    var quickDecisionClickListener: (View, QuickDecisionModel, Int) -> Unit = { _, _, _ -> }
        set(value) {
            quickDecisionDelegateAdapter.clickListener = value
        }
    var addNewClickListener: () -> Unit = {}
        set(value) {
            addNewDelegateAdapter.clickListener = value
        }
    var colorList: List<Int> = ArrayList()
        set(value) {
            quickDecisionDelegateAdapter.colorList = value
            addNewDelegateAdapter.colorList = value
        }
}
