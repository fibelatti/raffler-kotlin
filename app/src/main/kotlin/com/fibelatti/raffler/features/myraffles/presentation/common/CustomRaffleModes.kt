package com.fibelatti.raffler.features.myraffles.presentation.common

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.fibelatti.core.extension.visibleIf
import com.fibelatti.raffler.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_custom_raffle_modes.*

interface CustomRaffleModes {

    fun showRaffleModes(
        context: Context,
        rouletteClickListener: (() -> Unit)? = null,
        randomWinnersClickListener: (() -> Unit)? = null,
        groupingClickListener: (() -> Unit)? = null,
        combinationClickListener: (() -> Unit)? = null,
        votingClickListener: (() -> Unit)? = null
    )
}

class CustomRaffleModesDelegate : CustomRaffleModes {

    override fun showRaffleModes(
        context: Context,
        rouletteClickListener: (() -> Unit)?,
        randomWinnersClickListener: (() -> Unit)?,
        groupingClickListener: (() -> Unit)?,
        combinationClickListener: (() -> Unit)?,
        votingClickListener: (() -> Unit)?
    ) {
        BottomSheetDialog(context, R.style.AppTheme_BaseBottomSheetDialog_BottomSheetDialog).apply {
            setContentView(R.layout.fragment_custom_raffle_modes)

            setupMode(rouletteClickListener, layoutRoulette, buttonRoulette)
            setupMode(randomWinnersClickListener, layoutRandomWinners, buttonRandomWinners)
            setupMode(groupingClickListener, layoutGrouping, buttonGrouping)
            setupMode(combinationClickListener, layoutCombination, buttonCombination)
            setupMode(votingClickListener, layoutVoting, buttonVoting)

            setOnShowListener {
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            show()
        }
    }

    private fun BottomSheetDialog.setupMode(
        clickListener: (() -> Unit)?,
        layoutView: View?,
        button: Button?
    ) {
        val onClick = {
            setOnDismissListener { clickListener?.invoke() }
            dismiss()
        }

        layoutView?.apply {
            visibleIf(clickListener != null, otherwiseVisibility = View.GONE)
            setOnClickListener { onClick() }
        }
        button?.setOnClickListener { onClick() }
    }
}
