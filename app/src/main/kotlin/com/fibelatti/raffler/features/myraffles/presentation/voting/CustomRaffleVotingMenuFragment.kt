package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_menu.*
import java.text.MessageFormat
import javax.inject.Inject

class CustomRaffleVotingMenuFragment @Inject constructor() : BaseFragment(),
    CustomRaffleVotingPinConfirmation by CustomRaffleVotingPinConfirmationDelegate() {

    private val customRaffleVotingViewModel by activityViewModel { viewModelProvider.customRaffleVotingViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customRaffleVotingViewModel.run {
            observe(voting, ::showVotingDetails)
            observeEvent(pinError, ::handlePinError)
            observeEvent(results) { goToResults() }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_voting_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()

        withVoting { showVotingDetails(it) }
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { layoutRoot.findNavController().navigateUp() }

        buttonNewVote.setOnClickListener {
            layoutRoot.findNavController().navigate(
                R.id.action_fragmentCustomRaffleVotingMenu_to_fragmentCustomRaffleVotingVote
            )
        }

        buttonSeeResults.setOnClickListener {
            showPinConfirmation(requireContext(), customRaffleVotingViewModel::getVotingResults)
        }
    }

    private fun showVotingDetails(voting: CustomRaffleVotingModel) {
        layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, voting.description))
        textViewTotalVotes.text =
            MessageFormat.format(
                resources.getText(R.string.custom_raffle_voting_votes).toString(),
                voting.totalVotes
            )
    }

    private fun handlePinError(message: String) {
        showPinError(message)
    }

    private fun goToResults() {
        dismissPinConfirmation()
        layoutRoot.hideKeyboard()
        layoutRoot.findNavController().navigate(
            R.id.action_fragmentCustomRaffleVotingMenu_to_fragmentCustomRaffleVotingResults
        )
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        customRaffleVotingViewModel.voting.value?.let(body)
    }
}
