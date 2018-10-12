package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.visibleIf
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import kotlinx.android.synthetic.main.list_item_custom_raffle_item.view.*
import javax.inject.Inject

class CustomRaffleDetailsAdapter @Inject constructor() : RecyclerView.Adapter<CustomRaffleDetailsAdapter.DataViewHolder>() {

    var clickListener: (index: Int, isSelected: Boolean) -> Unit = { _, _ -> }

    private val items: MutableList<CustomRaffleItemModel> = mutableListOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder = DataViewHolder(parent)

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: List<CustomRaffleItemModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_custom_raffle_item)
    ) {
        fun bind(item: CustomRaffleItemModel) = with(itemView) {
            item.run {
                layoutRootCustomRaffleItem.isSelected = included
                imageViewSelected.visibleIf(included, otherwiseVisibility = View.INVISIBLE)
                textViewCustomRaffleItemDescription.text = description

                setOnClickListener {
                    layoutRootCustomRaffleItem.isSelected = !layoutRootCustomRaffleItem.isSelected
                    imageViewSelected.visibleIf(layoutRootCustomRaffleItem.isSelected, otherwiseVisibility = View.INVISIBLE)
                    clickListener(adapterPosition, layoutRootCustomRaffleItem.isSelected)
                }
            }
        }
    }
}
