package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_menu.*

class CustomRaffleVotingMenuFragment : BaseFragment() {

    private val customRaffleVotingViewModel by lazy {
        viewModelFactory.get<CustomRaffleVotingViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customRaffleVotingViewModel.run {
            observe(voting, ::showVotingDetails)
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
        layoutTitle.setNavigateUp { layoutRoot.findNavController().navigateUp() }

        buttonNewVote.setOnClickListener {
            layoutRoot.findNavController().navigate(
                R.id.action_fragmentCustomRaffleVotingMenu_to_fragmentCustomRaffleVotingVote
            )
        }

        buttonSeeResults.setOnClickListener {
            it.snackbar("See results clicked")
        }
    }

    private fun showVotingDetails(voting: CustomRaffleVotingModel) {
        layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, voting.description))
        textViewTotalVotes.text = resources.getQuantityString(R.plurals.custom_raffle_voting_votes, voting.totalVotes, voting.totalVotes)
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        customRaffleVotingViewModel.voting.value?.let(body)
    }
}
