package com.fibelatti.raffler.features.myraffles.presentation

import android.content.Context
import com.fibelatti.raffler.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_custom_raffle_modes.*

interface CustomRaffleModes {
    fun showRaffleModes(
        context: Context,
        rouletteClickListener: () -> Unit
    )
}

class CustomRaffleModesDelegate : CustomRaffleModes {
    override fun showRaffleModes(
        context: Context,
        rouletteClickListener: () -> Unit
    ) {
        BottomSheetDialog(context, R.style.AppTheme_BaseBottomSheetDialog_BottomSheetDialog).apply {
            setContentView(R.layout.fragment_custom_raffle_modes)

            buttonRoulette?.setOnClickListener {
                rouletteClickListener()
                dismiss()
            }

            show()
        }
    }
}
