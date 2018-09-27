package com.fibelatti.raffler.core.platform

import android.support.v4.util.SparseArrayCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseAdapter : ListAdapter<BaseViewType, RecyclerView.ViewHolder>(
    ListAdapterBaseViewTypeCallback) {

    abstract val delegateAdapters: SparseArrayCompat<BaseDelegateAdapter>

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters[getItemViewType(position)].onBindViewHolder(holder, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateAdapters[viewType].onCreateViewHolder(parent)

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
    override fun areItemsTheSame(oldItem: BaseViewType?, newItem: BaseViewType?): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BaseViewType?, newItem: BaseViewType?): Boolean {
        return oldItem == newItem
    }
}
