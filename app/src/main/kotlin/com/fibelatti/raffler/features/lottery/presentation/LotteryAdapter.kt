package com.fibelatti.raffler.features.lottery.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import kotlinx.android.synthetic.main.list_item_lottery.view.*
import javax.inject.Inject

class LotteryAdapter @Inject constructor() :
    ListAdapter<LotteryNumberModel, LotteryAdapter.DataViewHolder>(LotteryNumberModelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteryAdapter.DataViewHolder =
        DataViewHolder(parent)

    override fun onBindViewHolder(holder: LotteryAdapter.DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_lottery)
    ) {
        fun bind(item: LotteryNumberModel) = with(itemView) {
            item.run {
                textViewLotteryItem.text = value
            }
        }
    }
}

object LotteryNumberModelDiffCallback : DiffUtil.ItemCallback<LotteryNumberModel>() {
    override fun areItemsTheSame(oldItem: LotteryNumberModel, newItem: LotteryNumberModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: LotteryNumberModel, newItem: LotteryNumberModel): Boolean {
        return oldItem === newItem
    }
}
