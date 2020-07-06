package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.extension.clearError
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.showKeyboard
import com.fibelatti.core.extension.textAsString
import com.fibelatti.core.extension.visible
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_start.*
import javax.inject.Inject

class CustomRaffleVotingStartFragment @Inject constructor() : BaseFragment(
    R.layout.fragment_custom_raffle_voting_start
) {

    private val customRaffleDetailsViewModel by activityViewModel {
        viewModelProvider.customRaffleDetailsViewModel()
    }
    private val customRaffleVotingViewModel by activityViewModel {
        viewModelProvider.customRaffleVotingViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.observe(customRaffleDetailsViewModel.customRaffle) {
            setupLayout(it)
            customRaffleVotingViewModel.checkForOngoingVoting(it)
        }

        viewLifecycleOwner.observe(customRaffleVotingViewModel.ongoingVoting, ::handleOngoingVoting)
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.readyToVote) { handleReadyToVote() }
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.pinError, ::handlePinError)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    private fun setupLayout(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }
        layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, customRaffleModel.description))

        buttonStartVoting.setOnClickListener {
            customRaffleVotingViewModel.setupNewVoting(editTextPin.textAsString(), customRaffleModel)
        }

        editTextPin.showKeyboard()
    }

    private fun handleOngoingVoting(customRaffleModel: CustomRaffleModel) {
        textViewOngoingVotingDescription.visible()
        layoutDivider.visible()
        textViewStartVotingPinInstructions.setText(R.string.custom_raffle_voting_pin_instructions_existing)

        buttonStartVoting.setText(R.string.custom_raffle_voting_continue_voting)
        buttonStartVoting.setOnClickListener {
            customRaffleVotingViewModel.resumeVoting(editTextPin.textAsString())
        }

        buttonResetVoting.visible()
        buttonResetVoting.setOnClickListener {
            customRaffleVotingViewModel.setupNewVoting(editTextPin.textAsString(), customRaffleModel)
        }
    }

    private fun handleReadyToVote() {
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleVotingStart_to_fragmentCustomRaffleVotingMenu
        )
    }

    private fun handlePinError(message: String) {
        if (message.isNotBlank()) {
            inputLayoutPin.showError(message)
            editTextPin.isError = true
        } else {
            inputLayoutPin.clearError()
            editTextPin.isError = false
        }
    }
}
