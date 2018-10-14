package com.fibelatti.raffler.features.myraffles.presentation.list

import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import com.fibelatti.raffler.core.platform.base.BaseAdapter
import com.fibelatti.raffler.core.platform.base.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.core.platform.recyclerview.AddNewDelegateAdapter
import com.fibelatti.raffler.core.platform.recyclerview.AddNewModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import javax.inject.Inject

class CustomRaffleAdapter @Inject constructor(
    private val customRaffleDelegateAdapter: CustomRaffleDelegateAdapter,
    private val addNewDelegateAdapter: AddNewDelegateAdapter
) : BaseAdapter(CustomRaffleModelDiffCallback) {

    override val delegateAdapters: SparseArrayCompat<BaseDelegateAdapter> by lazy {
        SparseArrayCompat<BaseDelegateAdapter>().apply {
            put(AddNewModel.VIEW_TYPE, addNewDelegateAdapter)
            put(CustomRaffleModel.VIEW_TYPE, customRaffleDelegateAdapter)
        }
    }

    var customRaffleClickListener: (Long) -> Unit = { }
        set(value) {
            customRaffleDelegateAdapter.clickListener = value
        }
    var addNewClickListener: () -> Unit = {}
        set(value) {
            addNewDelegateAdapter.clickListener = value
        }
    var colorList: List<Int> = ArrayList()
        set(value) {
            customRaffleDelegateAdapter.colorList = value
            addNewDelegateAdapter.colorList = value
        }
}

object CustomRaffleModelDiffCallback : DiffUtil.ItemCallback<BaseViewType>() {
    override fun areItemsTheSame(oldItem: BaseViewType, newItem: BaseViewType): Boolean {
        return when {
            oldItem is CustomRaffleModel && newItem is CustomRaffleModel -> oldItem.id == newItem.id
            else -> oldItem == newItem
        }
    }

    override fun areContentsTheSame(oldItem: BaseViewType, newItem: BaseViewType): Boolean {
        return oldItem == newItem
    }
}
