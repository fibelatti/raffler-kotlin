package com.fibelatti.raffler.features.preferences.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.clearError
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.showError
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.extension.textAsString
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences_lottery.*

class PreferencesLotteryFragment : BaseFragment() {

    private val preferencesViewModel by lazy {
        viewModelFactory.get<PreferencesViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesViewModel.run {
            error(error, ::handleError)
            observe(preferences) {
                setupDefaultLotteryValues(
                    it.lotteryDefaultQuantityAvailable,
                    it.lotteryDefaultQuantityToRaffle
                )
            }
            observeEvent(updateFeedback) {
                layoutRoot.hideKeyboard()
                layoutRoot.snackbar(it)
            }
            observeEvent(totalQuantityError, ::handleTotalQuantityError)
            observeEvent(raffleQuantityError, ::handleRaffleQuantityError)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preferences_lottery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        preferencesViewModel.getPreferences()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.preferences_section_lottery)
        layoutTitle.navigateUp { layoutRoot.findNavController().navigateUp() }

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
