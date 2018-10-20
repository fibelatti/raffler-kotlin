package com.fibelatti.raffler.core.platform.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.core.extension.inflate

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseAdapter<T>.ViewHolder>() {

    protected val items: MutableList<T> = mutableListOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    abstract fun View.bindView(item: T, viewHolder: ViewHolder)

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(getLayoutRes())
    ) {
        fun bind(item: T) = itemView.bindView(item, this)
    }
}

abstract class BaseAdapterWithDelegates : RecyclerView.Adapter<BaseAdapterWithDelegates.ViewHolder>() {

    protected val items: MutableList<BaseViewType> = mutableListOf()

    protected val delegateAdapters: SparseArrayCompat<BaseDelegateAdapter> = SparseArrayCompat()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseAdapterWithDelegates.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapterWithDelegates.ViewHolder =
        delegateAdapters[viewType]?.getLayoutRes()?.let { ViewHolder(parent, it) }
            ?: throw RuntimeException("No adapter mapped to viewType: $viewType")

    override fun getItemViewType(position: Int): Int = items[position].getViewType()

    fun setItems(items: List<BaseViewType>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        parent: ViewGroup,
        @LayoutRes layoutRes: Int
    ) : RecyclerView.ViewHolder(parent.inflate(layoutRes)) {

        fun bind(item: BaseViewType) = delegateAdapters[item.getViewType()]?.bindView(itemView, item, this)
    }
}

interface BaseDelegateAdapter {
    @LayoutRes
    fun getLayoutRes(): Int

    fun bindView(itemView: View, item: BaseViewType, viewHolder: RecyclerView.ViewHolder)
}

interface BaseViewType {
    fun getViewType(): Int
}
