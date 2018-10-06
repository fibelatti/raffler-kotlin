package com.fibelatti.raffler.features.myraffles.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.features.myraffles.presentation.CustomRaffleDraftedModel
import kotlinx.android.synthetic.main.list_item_custom_raffle_drafted_item.view.*
import javax.inject.Inject

class CustomRaffleDraftedAdapter @Inject constructor() : RecyclerView.Adapter<CustomRaffleDraftedAdapter.DataViewHolder>() {

    var colorList: List<Int> = ArrayList()

    private val items: MutableList<CustomRaffleDraftedModel> = mutableListOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(parent)

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: List<CustomRaffleDraftedModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_custom_raffle_drafted_item)
    ) {
        fun bind(item: CustomRaffleDraftedModel) = with(itemView) {
            layoutRootModeItem.setShapeBackgroundColor(colorList[layoutPosition % colorList.size])
            textViewTitle.text = item.title
            textViewItems.text = item.description
        }
    }
}
