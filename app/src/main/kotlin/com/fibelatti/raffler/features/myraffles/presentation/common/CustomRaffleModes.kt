package com.fibelatti.raffler.features.myraffles.presentation.common

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.visibleIf
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

            setupMode(rouletteClickListener, groupRouletteViews, buttonRoulette)
            setupMode(randomWinnersClickListener, groupRandomWinnersViews, buttonRandomWinners)
            setupMode(groupingClickListener, groupGroupingViews, buttonGrouping)
            setupMode(combinationClickListener, groupCombinationViews, buttonCombination)
            setupMode(votingClickListener, groupVotingViews, buttonVoting)

            setOnShowListener {
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            show()
        }
    }

    private fun BottomSheetDialog.setupMode(clickListener: (() -> Unit)?, groupView: View?, buttonView: Button?) {
        groupView?.visibleIf(clickListener != null)
        buttonView?.setOnClickListener {
            setOnDismissListener { clickListener?.invoke() }
            dismiss()
        }
    }
}
