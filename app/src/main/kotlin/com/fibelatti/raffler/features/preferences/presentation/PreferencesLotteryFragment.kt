package com.fibelatti.raffler.features.preferences.presentation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.clearError
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.snackbar
import com.fibelatti.core.extension.textAsString
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences_lottery.*
import javax.inject.Inject

class PreferencesLotteryFragment @Inject constructor() : BaseFragment(R.layout.fragment_preferences_lottery) {

    private val preferencesViewModel by viewModel { viewModelProvider.preferencesViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()

        viewLifecycleOwner.observe(preferencesViewModel.error, ::handleError)
        viewLifecycleOwner.observe(preferencesViewModel.preferences) {
            setupDefaultLotteryValues(
                it.lotteryDefaultQuantityAvailable,
                it.lotteryDefaultQuantityToRaffle
            )
        }
        viewLifecycleOwner.observeEvent(preferencesViewModel.updateFeedback) {
            layoutRoot.hideKeyboard()
            layoutRoot.snackbar(
                message = it,
                background = R.drawable.background_snackbar,
                textColor = R.color.text_primary
            )
        }
        viewLifecycleOwner.observeEvent(preferencesViewModel.totalQuantityError, ::handleTotalQuantityError)
        viewLifecycleOwner.observeEvent(preferencesViewModel.raffleQuantityError, ::handleRaffleQuantityError)

        preferencesViewModel.getPreferences()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.preferences_section_lottery)
        layoutTitle.setNavigateUp { findNavController().navigateUp() }

        buttonLotteryDefaults.setOnClickListener {
            preferencesViewModel.setLotteryDefaultValues(
                editTextTotalQuantity.textAsString(),
                editTextRaffleQuantity.textAsString()
            )
        }
    }

    private fun setupDefaultLotteryValues(quantityAvailable: String, quantityToRaffle: String) {
        editTextTotalQuantity.setText(quantityAvailable)
        editTextRaffleQuantity.setText(quantityToRaffle)
    }

    private fun handleTotalQuantityError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutTotalQuantity.showError(message)
        } else {
            inputLayoutTotalQuantity.clearError()
        }
    }

    private fun handleRaffleQuantityError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutRaffleQuantity.showError(message)
        } else {
            inputLayoutRaffleQuantity.clearError()
        }
    }
}
