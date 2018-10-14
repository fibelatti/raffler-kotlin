package com.fibelatti.raffler.features.myraffles.presentation.common

import android.content.Context
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.AddNewQuickDecisionAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_add_custom_raffle_as_quick_decision.*

interface CustomRaffleSelector {
    fun showCustomRaffleSelector(
        context: Context,
        title: String,
        customRaffles: List<CustomRaffleModel>,
        customRaffleClickListener: (CustomRaffleModel) -> Unit
    )
}

class CustomRaffleSelectorDelegate : CustomRaffleSelector {
    override fun showCustomRaffleSelector(
        context: Context,
        title: String,
        customRaffles: List<CustomRaffleModel>,
        customRaffleClickListener: (CustomRaffleModel) -> Unit
    ) {
        BottomSheetDialog(context, R.style.AppTheme_BaseBottomSheetDialog_BottomSheetDialog).apply {
            setContentView(R.layout.fragment_add_custom_raffle_as_quick_decision)

            textViewSelectCustomRaffleTitle?.text = title

            val adapter = AddNewQuickDecisionAdapter().apply {
                clickListener = {
                    customRaffleClickListener(it)
                    dismiss()
                }

                setItems(customRaffles)
            }

            recyclerViewItems?.withDefaultDecoration()
                ?.withLinearLayoutManager()
                ?.adapter = adapter

            show()
        }
    }
}
