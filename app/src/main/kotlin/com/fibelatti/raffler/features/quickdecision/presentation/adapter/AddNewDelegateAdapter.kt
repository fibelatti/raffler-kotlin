package com.fibelatti.raffler.features.quickdecision.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.BaseDelegateAdapter
import com.fibelatti.raffler.core.platform.BaseViewType
import kotlinx.android.synthetic.main.list_item_add_new.view.*
import javax.inject.Inject

class AddNewDelegateAdapter @Inject constructor() : BaseDelegateAdapter {
    var colorList: List<Int> = ArrayList()
    var clickListener: () -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: BaseViewType) {
        (holder as? ViewHolder)?.bind()
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_add_new)
    ) {
        fun bind() = with(itemView) {
            layoutRoot.setShapeBackgroundColor(colorList[layoutPosition % colorList.size])
            setOnClickListener { clickListener() }
        }
    }
}
