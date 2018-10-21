package com.fibelatti.raffler.features.myraffles.presentation.voting.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsViewModel
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingModel
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingViewModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_results.*
import javax.inject.Inject

class CustomRaffleVotingResultsFragment : BaseFragment() {

    private val customRaffleVotingViewModel by lazy {
        viewModelFactory.get<CustomRaffleVotingViewModel>(requireActivity())
    }
    private val customRaffleDetailsViewModel by lazy {
        viewModelFactory.get<CustomRaffleDetailsViewModel>(requireActivity())
    }

    @Inject
    lateinit var adapter: CustomRaffleVotingResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        customRaffleVotingViewModel.run {
            observeEvent(showTieBreaker) { setupTieBreaker() }
            observeEvent(readyToVote) { startTieBreakVoting() }
            observeEvent(showRandomDecision, ::startRandomDecision)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_voting_results, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()

        withVoting {
            layoutTitle.setTitle(getString(R.string.custom_raffle_voting_results_title, it.description))
        }

        withResults { adapter.setItems(it) }
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { layoutRoot.findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withLinearLayoutManager()
            .adapter = adapter
    }

    private fun setupTieBreaker() {
        groupTieBreakerViews.visible()
        buttonVoteAgain.setOnClickListener { customRaffleVotingViewModel.setupTieBreakVoting() }
        buttonDecideRandomly.setOnClickListener { withCustomRaffle(customRaffleVotingViewModel::setupRandomDecision) }
    }

    private fun startTieBreakVoting() {
        layoutRoot.findNavController().navigate(
            R.id.fragmentCustomRaffleVotingMenu,
            null,
            navOptions { popUpTo = R.id.fragmentCustomRaffleDetails }
        )
    }

    private fun startRandomDecision(customRaffle: CustomRaffleModel) {
        customRaffleDetailsViewModel.setCustomRaffleForContinuation(customRaffle)
        layoutRoot.findNavController().navigate(
            R.id.fragmentCustomRaffleRoulette,
            null,
            navOptions { popUpTo = R.id.fragmentCustomRaffleDetails }
        )
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        customRaffleVotingViewModel.voting.value?.let(body)
    }

    private fun withResults(body: (List<BaseViewType>) -> Unit) {
        customRaffleVotingViewModel.results.value?.peekContent()?.let(body)
    }

    private fun withCustomRaffle(body: (CustomRaffleModel) -> Unit) {
        customRaffleDetailsViewModel.customRaffle.value?.let(body)
    }
}
