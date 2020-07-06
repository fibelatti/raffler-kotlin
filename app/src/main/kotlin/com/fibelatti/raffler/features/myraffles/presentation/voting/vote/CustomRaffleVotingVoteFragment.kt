package com.fibelatti.raffler.features.myraffles.presentation.voting.vote

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.alertDialogBuilder
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_vote.*
import javax.inject.Inject

class CustomRaffleVotingVoteFragment @Inject constructor(
    private val customRaffleVotingVoteAdapter: CustomRaffleVotingVoteAdapter
) : BaseFragment(R.layout.fragment_custom_raffle_voting_vote) {

    private val customRaffleVotingViewModel by activityViewModel {
        viewModelProvider.customRaffleVotingViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewLifecycleOwner.observe(customRaffleVotingViewModel.voting, ::setupLayout)
        viewLifecycleOwner.observeEvent(customRaffleVotingViewModel.readyToVote) {
            findNavController().navigateUp()
        }
    }

    private fun setupLayout(customRaffleVotingModel: CustomRaffleVotingModel) {
        layoutTitle.setNavigateUp { findNavController().navigateUp() }
        layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, customRaffleVotingModel.description))
        customRaffleVotingVoteAdapter.submitList(customRaffleVotingModel.votes.map { item -> item.key })
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withLinearLayoutManager()
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = customRaffleVotingVoteAdapter

        customRaffleVotingVoteAdapter.clickListener = { vote ->
            alertDialogBuilder {
                setMessage(getString(R.string.custom_raffle_voting_confirm_vote, vote))
                setPositiveButton(R.string.hint_yes) { _, _ -> customRaffleVotingViewModel.vote(vote) }
                setNegativeButton(R.string.hint_no) { dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }
}
