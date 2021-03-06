package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.android.BundleDelegate
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.extension.exhaustive
import com.fibelatti.core.extension.gone
import com.fibelatti.core.extension.showStyledDialog
import com.fibelatti.core.extension.visible
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModes
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModesDelegate
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_details.*
import javax.inject.Inject

private var Bundle.customRaffleId by BundleDelegate.Long("CUSTOM_RAFFLE_ID")

class CustomRaffleDetailsFragment @Inject constructor(
    private val customRaffleDetailsAdapter: CustomRaffleDetailsAdapter
) : BaseFragment(R.layout.fragment_custom_raffle_details),
    CustomRaffleModes by CustomRaffleModesDelegate() {

    companion object {
        fun bundle(
            customRaffleId: Long
        ) = Bundle().apply {
            this.customRaffleId = customRaffleId
        }
    }

    private val customRaffleDetailsViewModel by activityViewModel { viewModelProvider.customRaffleDetailsViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()

        customRaffleDetailsViewModel.run {
            viewLifecycleOwner.observe(error, ::handleError)
            viewLifecycleOwner.observe(preferredRaffleMode, ::setupRaffleButtons)
            viewLifecycleOwner.observe(customRaffle, ::showCustomRaffleDetails)
            viewLifecycleOwner.observeEvent(showHint) {
                showDismissibleHint(
                    container = layoutHintContainer,
                    hintTitle = getString(R.string.hint_quick_tip),
                    hintMessage = getString(R.string.custom_raffle_details_dismissible_hint),
                    onHintDismissed = { customRaffleDetailsViewModel.hintDismissed() }
                )
            }
            viewLifecycleOwner.observeEvent(invalidSelectionError, ::handleInvalidSelectionError)
            viewLifecycleOwner.observeEvent(showModeSelector) { showRaffleModeSelector() }
            viewLifecycleOwner.observeEvent(showPreferredRaffleMode) { showPreferredRaffleMode(it) }
            viewLifecycleOwner.observeEvent(toggleState, ::handleToggleState)
        }
    }

    override fun onResume() {
        super.onResume()
        customRaffleDetailsViewModel.getCustomRaffleById(arguments?.customRaffleId)
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }

        buttonEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_fragmentCustomRaffleDetails_to_fragmentCreateCustomRaffle,
                CreateCustomRaffleFragment.bundle(customRaffleId = arguments?.customRaffleId),
                CreateCustomRaffleFragment.navOptionsEdit()
            )
        }

        textViewToggleAll.setOnClickListener { customRaffleDetailsViewModel.toggleAll() }
    }

    private fun setupRaffleButtons(raffleMode: AppConfig.RaffleMode) {
        buttonRaffle.visible()
        when (raffleMode) {
            AppConfig.RaffleMode.NONE -> {
                buttonSelectMode.gone()
                buttonRaffle.setOnClickListener { customRaffleDetailsViewModel.selectMode() }
            }
            else -> {
                buttonSelectMode.visible()
                buttonSelectMode.setOnClickListener { customRaffleDetailsViewModel.selectMode() }
                buttonRaffle.setOnClickListener { customRaffleDetailsViewModel.raffle() }
            }
        }.exhaustive
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withLinearLayoutManager()
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = customRaffleDetailsAdapter

        customRaffleDetailsAdapter.clickListener = customRaffleDetailsViewModel::updateItemSelection
    }

    private fun showCustomRaffleDetails(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setTitle(customRaffleModel.description)
        customRaffleDetailsAdapter.submitList(customRaffleModel.items)
    }

    private fun handleInvalidSelectionError(message: String) {
        context?.showStyledDialog(
            dialogStyle = R.style.AppTheme_AlertDialog,
            dialogBackground = R.drawable.background_contrast_rounded
        ) {
            setMessage(message)
            setPositiveButton(R.string.hint_ok) { dialog, _ -> dialog.dismiss() }
        }
    }

    private fun showRaffleModeSelector() {
        showRaffleModes(
            requireContext(),
            rouletteClickListener = { goToRoulette() },
            randomWinnersClickListener = { goToRandomWinners() },
            groupingClickListener = { goToGrouping() },
            combinationClickListener = { goToCombination() },
            votingClickListener = { goToVoting() }
        )
    }

    private fun showPreferredRaffleMode(raffleMode: AppConfig.RaffleMode) {
        when (raffleMode) {
            AppConfig.RaffleMode.ROULETTE -> goToRoulette()
            AppConfig.RaffleMode.RANDOM_WINNERS -> goToRandomWinners()
            AppConfig.RaffleMode.GROUPING -> goToGrouping()
            AppConfig.RaffleMode.COMBINATION -> goToCombination()
            AppConfig.RaffleMode.SECRET_VOTING -> goToVoting()
            AppConfig.RaffleMode.NONE -> showRaffleModeSelector()
        }.exhaustive
    }

    private fun handleToggleState(toggleState: CustomRaffleDetailsViewModel.ToggleState) {
        textViewToggleAll.setText(
            when (toggleState) {
                CustomRaffleDetailsViewModel.ToggleState.INCLUDE_ALL -> R.string.custom_raffle_details_include_all
                CustomRaffleDetailsViewModel.ToggleState.EXCLUDE_ALL -> R.string.custom_raffle_details_exclude_all
            }
        )
    }

    private fun goToRoulette() {
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleRoulette
        )
    }

    private fun goToRandomWinners() {
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleRandomWinners
        )
    }

    private fun goToGrouping() {
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleGrouping
        )
    }

    private fun goToCombination() {
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleCombination
        )
    }

    private fun goToVoting() {
        findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleVotingStart
        )
    }
}
