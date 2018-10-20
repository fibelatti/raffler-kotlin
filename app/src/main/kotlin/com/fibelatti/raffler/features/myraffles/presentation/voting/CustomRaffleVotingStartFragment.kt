package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.clearError
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.showError
import com.fibelatti.raffler.core.extension.showKeyboard
import com.fibelatti.raffler.core.extension.textAsString
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsViewModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_start.*

class CustomRaffleVotingStartFragment : BaseFragment() {

    private val customRaffleDetailsViewModel by lazy {
        viewModelFactory.get<CustomRaffleDetailsViewModel>(requireActivity())
    }
    private val customRaffleVotingViewModel by lazy {
        viewModelFactory.get<CustomRaffleVotingViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customRaffleVotingViewModel.run {
            observeEvent(ongoingVoting) { handleOngoingVoting() }
            observeEvent(readyToVote) { handleReadyToVote() }
            observeEvent(pinError, ::handlePinError)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_voting_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()

        withCustomRaffle { customRaffleVotingViewModel.checkForOngoingVoting(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { layoutRoot.findNavController().navigateUp() }

        withCustomRaffle { customRaffle ->
            layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, customRaffle.description))

            buttonStartVoting.setOnClickListener {
                customRaffleVotingViewModel.setupNewVoting(
                    pin = editTextPin.textAsString(),
                    customRaffle = customRaffle
                )
            }
        }

        editTextPin.showKeyboard()
    }

    private fun handleOngoingVoting() {
        textViewOngoingVotingDescription.visible()
        layoutDivider.visible()
        textViewStartVotingPinInstructions.setText(R.string.custom_raffle_voting_pin_instructions_existing)
        buttonResetVoting.visible()
        buttonStartVoting.setText(R.string.custom_raffle_voting_continue_voting)

        buttonResetVoting.setOnClickListener {
            withCustomRaffle { customRaffle ->
                customRaffleVotingViewModel.setupNewVoting(
                    pin = editTextPin.textAsString(),
                    customRaffle = customRaffle
                )
            }
        }

        buttonStartVoting.setOnClickListener {
            customRaffleVotingViewModel.resumeVoting(editTextPin.textAsString())
        }
    }

    private fun handleReadyToVote() {
        layoutRoot.findNavController().navigate(R.id.action_fragmentCustomRaffleVotingStart_to_fragmentCustomRaffleVotingMenu)
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

    private fun withCustomRaffle(body: (CustomRaffleModel) -> Unit) {
        customRaffleDetailsViewModel.customRaffle.value?.let(body)
    }
}
