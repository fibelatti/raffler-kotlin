package com.fibelatti.raffler.core.platform

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter : ListAdapter<BaseViewType, RecyclerView.ViewHolder>(
    ListAdapterBaseViewTypeCallback) {

    abstract val delegateAdapters: SparseArrayCompat<BaseDelegateAdapter>

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters[getItemViewType(position)]?.onBindViewHolder(holder, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateAdapters[viewType]?.let {
            it.onCreateViewHolder(parent)
        } ?: throw RuntimeException("No adapter mapped to viewType: $viewType")

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
