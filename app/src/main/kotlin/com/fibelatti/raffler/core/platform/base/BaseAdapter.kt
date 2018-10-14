package com.fibelatti.raffler.core.platform.base

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter(
    diffUtilCallback: DiffUtil.ItemCallback<BaseViewType> = ListAdapterBaseViewTypeCallback
) : ListAdapter<BaseViewType, RecyclerView.ViewHolder>(diffUtilCallback) {

    abstract val delegateAdapters: SparseArrayCompat<BaseDelegateAdapter>

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters[getItemViewType(position)]?.onBindViewHolder(holder, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateAdapters[viewType]?.onCreateViewHolder(parent)
            ?: throw RuntimeException("No adapter mapped to viewType: $viewType")

    override fun getItemViewType(position: Int): Int = getItem(position).getViewType()
}

interface BaseDelegateAdapter {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: BaseViewType)
}

interface BaseViewType {
    fun getViewType(): Int
}

object ListAdapterBaseViewTypeCallback : DiffUtil.ItemCallback<BaseViewType>() {
    override fun areItemsTheSame(oldItem: BaseViewType, newItem: BaseViewType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BaseViewType, newItem: BaseViewType): Boolean {
        return oldItem == newItem
    }
}
