package com.fibelatti.raffler.features.myraffles.presentation.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.alertDialogBuilder
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_voting_vote.*
import javax.inject.Inject

class CustomRaffleVotingVoteFragment : BaseFragment() {

    private val customRaffleVotingViewModel by lazy {
        viewModelFactory.get<CustomRaffleVotingViewModel>(requireActivity())
    }

    @Inject
    lateinit var adapter: CustomRaffleVotingVoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        customRaffleVotingViewModel.run {
            observeEvent(readyToVote) { handleReadyToVote() }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_voting_vote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()

        withVoting {
            layoutTitle.setTitle(getString(R.string.custom_raffle_voting_title, it.description))
            adapter.setItems(it.votes.map { item -> item.key })
        }
    }

    private fun setupLayout() {
        layoutTitle.navigateUp { layoutRoot.findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withLinearLayoutManager()
            .adapter = adapter

        adapter.clickListener = { vote ->
            alertDialogBuilder {
                setMessage(getString(R.string.custom_raffle_voting_confirm_vote, vote))
                setPositiveButton(R.string.hint_yes) { _, _ ->
                    customRaffleVotingViewModel.vote(vote)
                }
                setNegativeButton(R.string.hint_no) { dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }

    private fun handleReadyToVote() {
        layoutRoot.findNavController().navigateUp()
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        customRaffleVotingViewModel.voting.value?.let(body)
    }
}
