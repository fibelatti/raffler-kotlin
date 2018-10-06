package com.fibelatti.raffler.features.myraffles.presentation.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.visibleIf
import com.fibelatti.raffler.features.myraffles.presentation.CustomRaffleItemModel
import kotlinx.android.synthetic.main.list_item_custom_raffle_item.view.*

import javax.inject.Inject

class CustomRaffleDetailsAdapter
@Inject constructor() : ListAdapter<CustomRaffleItemModel, CustomRaffleDetailsAdapter.DataViewHolder>(
    CustomRaffleItemModelDiffCallback
) {

    var clickListener: (CustomRaffleItemModel, Boolean) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder = DataViewHolder(parent)

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_custom_raffle_item)
    ) {
        fun bind(item: CustomRaffleItemModel) = with(itemView) {
            item.run {
                layoutRootCustomRaffleItem.isSelected = true
                textViewCustomRaffleItemDescription.text = description

                setOnClickListener {
                    layoutRootCustomRaffleItem.isSelected = !layoutRootCustomRaffleItem.isSelected
                    imageViewSelected.visibleIf(layoutRootCustomRaffleItem.isSelected, otherwiseVisibility = View.INVISIBLE)
                    clickListener(item, layoutRootCustomRaffleItem.isSelected)
                }
            }
        }
    }
}

object CustomRaffleItemModelDiffCallback : DiffUtil.ItemCallback<CustomRaffleItemModel>() {
    override fun areItemsTheSame(oldItem: CustomRaffleItemModel, newItem: CustomRaffleItemModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CustomRaffleItemModel, newItem: CustomRaffleItemModel): Boolean {
        return oldItem == newItem
    }
}
