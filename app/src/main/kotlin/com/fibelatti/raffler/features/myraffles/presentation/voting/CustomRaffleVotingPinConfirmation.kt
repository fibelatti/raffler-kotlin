package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.content.Context
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.showKeyboard
import com.fibelatti.core.extension.textAsString
import com.fibelatti.raffler.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_custom_raffle_voting_pin_confirmation.*

interface CustomRaffleVotingPinConfirmation {

    fun showPinConfirmation(context: Context, onPinEntered: (pin: String) -> Unit)

    fun showPinError(message: String)

    fun dismissPinConfirmation()
}

class CustomRaffleVotingPinConfirmationDelegate : CustomRaffleVotingPinConfirmation {

    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun showPinConfirmation(context: Context, onPinEntered: (pin: String) -> Unit) {
        BottomSheetDialog(context, R.style.AppTheme_BottomSheet).apply {
            setContentView(R.layout.layout_custom_raffle_voting_pin_confirmation)
            editTextPin.onMaxLengthReached = { onPinEntered(editTextPin.textAsString()) }
            editTextPin.onBackPressed = {
                dismiss()
                hideKeyboard()
            }
            setOnShowListener { editTextPin.showKeyboard() }
            show()
        }.also { bottomSheetDialog = it }
    }

    override fun showPinError(message: String) {
        bottomSheetDialog?.run {
            inputLayoutPin?.showError(message)
            layoutRoot.showKeyboard()
        }
    }

    override fun dismissPinConfirmation() {
        bottomSheetDialog?.dismiss()
    }
}
