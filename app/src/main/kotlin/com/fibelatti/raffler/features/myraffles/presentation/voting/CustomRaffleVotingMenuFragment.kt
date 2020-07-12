package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_menu.*
import java.text.MessageFormat
import javax.inject.Inject

class CustomRaffleVotingMenuFragment @Inject constructor() : BaseFragment(
    R.layout.fragment_custom_raffle_voting_menu
), CustomRaffleVotingPinConfirmation by CustomRaffleVotingPinConfirmationDelegate() {

    private val customRaffleVotingViewModel by activityViewModel {
        viewModelProvider.customRaffleVotingViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()

        viewLifecycleOwner.observe(customRaffleVotingViewModel.voting, ::showVotingDetails)
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.showResults) { goToResults() }
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.pinError, ::showPinError)
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }

        buttonNewVote.setOnClickListener {
            findNavController().navigate(
                R.id.action_fragmentCustomRaffleVotingMenu_to_fragmentCustomRaffleVotingVote
            )
        }

        buttonSeeResults.setOnClickListener {
            showPinConfirmation(requireContext(), customRaffleVotingViewModel::getVotingResults)
        }
    }

    private fun showVotingDetails(voting: CustomRaffleVotingModel) {
        layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, voting.description))
        textViewTotalVotes.text = MessageFormat.format(
            resources.getText(R.string.custom_raffle_voting_votes).toString(),
            voting.totalVotes
        )
    }

    private fun goToResults() {
        dismissPinConfirmation()
        layoutRoot.hideKeyboard()
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleVotingMenu_to_fragmentCustomRaffleVotingResults
        )
    }
}
