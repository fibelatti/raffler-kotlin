package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.extension.visible
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_results.*
import javax.inject.Inject

class CustomRaffleVotingResultsFragment @Inject constructor(
    private val customRaffleVotingResultsAdapter: CustomRaffleVotingResultsAdapter
) : BaseFragment(R.layout.fragment_custom_raffle_voting_results) {

    private val customRaffleVotingViewModel by activityViewModel {
        viewModelProvider.customRaffleVotingViewModel()
    }
    private val customRaffleDetailsViewModel by activityViewModel {
        viewModelProvider.customRaffleDetailsViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.observe(customRaffleVotingViewModel.voting, ::setupLayout)
        viewLifecycleOwner.observe(
            customRaffleVotingViewModel.results,
            customRaffleVotingResultsAdapter::submitList
        )
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.showTieBreaker) { setupTieBreaker() }
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.readyToVote) { startTieBreakVoting() }
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.showRandomDecision, ::startRandomDecision)
    }

    private fun setupLayout(customRaffleVotingModel: CustomRaffleVotingModel) {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }
        layoutTitle.setTitle(
            getString(
                R.string.custom_raffle_voting_results_title,
                customRaffleVotingModel.description
            )
        )
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withLinearLayoutManager()
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = customRaffleVotingResultsAdapter
    }

    private fun setupTieBreaker() {
        groupTieBreakerViews.visible()
        buttonVoteAgain.setOnClickListener { customRaffleVotingViewModel.setupTieBreakVoting() }
        buttonDecideRandomly.setOnClickListener { customRaffleVotingViewModel.setupRandomDecision() }
    }

    private fun startTieBreakVoting() {
        findNavController().navigate(
            R.id.fragmentCustomRaffleVotingMenu,
            null,
            navOptions { popUpTo = R.id.fragmentCustomRaffleDetails }
        )
    }

    private fun startRandomDecision(customRaffle: CustomRaffleModel) {
        customRaffleDetailsViewModel.setCustomRaffleForContinuation(customRaffle)
        findNavController().navigate(
            R.id.fragmentCustomRaffleRoulette,
            null,
            navOptions { popUpTo = R.id.fragmentCustomRaffleDetails }
        )
    }
}
