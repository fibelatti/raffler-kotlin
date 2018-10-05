package com.fibelatti.raffler.features.myraffles.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.features.myraffles.presentation.CustomRaffleItemModel
import kotlinx.android.synthetic.main.list_item_create_custom_raffle_item.view.*
import javax.inject.Inject

class CreateCustomRaffleAdapter
@Inject constructor() : ListAdapter<CustomRaffleItemModel, CreateCustomRaffleAdapter.DataViewHolder>(
    CreateCustomRaffleItemModelDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder = DataViewHolder(parent)

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_create_custom_raffle_item)
    ) {
        fun bind(item: CustomRaffleItemModel) = with(itemView) {
            item.run {
                textViewCreateCustomRaffleItemDescription.text = description
            }
        }
    }
}

object CreateCustomRaffleItemModelDiffCallback : DiffUtil.ItemCallback<CustomRaffleItemModel>() {
    override fun areItemsTheSame(oldItem: CustomRaffleItemModel, newItem: CustomRaffleItemModel): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: CustomRaffleItemModel, newItem: CustomRaffleItemModel): Boolean {
        return oldItem == newItem
    }
}
