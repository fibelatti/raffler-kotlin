package com.fibelatti.raffler.features.quickdecision.presentation.addnew

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.list_item_add_new_quick_decision.view.*
import javax.inject.Inject

class AddNewQuickDecisionAdapter @Inject constructor() : RecyclerView.Adapter<AddNewQuickDecisionAdapter.DataViewHolder>() {

    var clickListener: (customRaffle: CustomRaffleModel) -> Unit = { }

    private val items: MutableList<CustomRaffleModel> = mutableListOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder = DataViewHolder(parent)

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: List<CustomRaffleModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class DataViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.list_item_add_new_quick_decision)
    ) {
        fun bind(item: CustomRaffleModel) = with(itemView) {
            item.run {
                textViewCustomRaffleDescription.text = description
                setOnClickListener { clickListener(item) }
            }
        }
    }
}
