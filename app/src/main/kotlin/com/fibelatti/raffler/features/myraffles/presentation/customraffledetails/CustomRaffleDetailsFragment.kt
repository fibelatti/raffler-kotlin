package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.alertDialogBuilder
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.exhaustive
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.BundleDelegate
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModes
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModesDelegate
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_details.*
import javax.inject.Inject

private var Bundle.customRaffleId by BundleDelegate.Long("CUSTOM_RAFFLE_ID")

class CustomRaffleDetailsFragment :
    BaseFragment(),
    CustomRaffleModes by CustomRaffleModesDelegate() {

    companion object {
        fun bundle(
            customRaffleId: Long
        ) = Bundle().apply {
            this.customRaffleId = customRaffleId
        }
    }

    @Inject
    lateinit var adapter: CustomRaffleDetailsAdapter

    private val customRaffleDetailsViewModel by lazy {
        viewModelFactory.get<CustomRaffleDetailsViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        customRaffleDetailsViewModel.run {
            error(error, ::handleError)
            observe(preferredRaffleMode, ::setupRaffleButtons)
            observe(customRaffle, ::showCustomRaffleDetails)
            observeEvent(showHint) {
                showDismissibleHint(
                    container = layoutHintContainer,
                    hintTitle = getString(R.string.hint_quick_tip),
                    hintMessage = getString(R.string.custom_raffle_details_dismissible_hint),
                    onHintDismissed = { customRaffleDetailsViewModel.hintDismissed() }
                )
            }
            observeEvent(invalidSelectionError, ::handleInvalidSelectionError)
            observeEvent(showModeSelector) { showRaffleModeSelector() }
            observeEvent(showPreferredRaffleMode) { showPreferredRaffleMode(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
        customRaffleDetailsViewModel.getCustomRaffleById(arguments?.customRaffleId)
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp { layoutRoot.findNavController().navigateUp() }

        buttonEdit.setOnClickListener {
            layoutRoot.findNavController().navigate(
                R.id.action_fragmentCustomRaffleDetails_to_fragmentCreateCustomRaffle,
                CreateCustomRaffleFragment.bundle(customRaffleId = arguments?.customRaffleId),
                CreateCustomRaffleFragment.navOptionsEdit()
            )
        }
    }

    private fun setupRaffleButtons(raffleMode: AppConfig.RaffleMode) {
        when (raffleMode) {
            AppConfig.RaffleMode.NONE -> {
                buttonSelectMode.gone()
                buttonRaffle.setOnClickListener { customRaffleDetailsViewModel.selectMode() }
            }
            else -> {
                buttonSelectMode.apply {
                    visible()
                    setOnClickListener { customRaffleDetailsViewModel.selectMode() }
                }
                buttonRaffle.setOnClickListener { customRaffleDetailsViewModel.raffle() }
            }
        }.exhaustive
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withLinearLayoutManager()
            .adapter = adapter

        adapter.clickListener = customRaffleDetailsViewModel::updateItemSelection
    }

    private fun showCustomRaffleDetails(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setTitle(customRaffleModel.description)
        adapter.setItems(customRaffleModel.items)
    }

    private fun handleInvalidSelectionError(message: String) {
        alertDialogBuilder {
            setMessage(message)
            setPositiveButton(R.string.hint_ok) { dialog, _ -> dialog.dismiss() }
            show()
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

    private fun goToRoulette() {
        layoutRoot.findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleRoulette
        )
    }

    private fun goToRandomWinners() {
        layoutRoot.findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleRandomWinners
        )
    }

    private fun goToGrouping() {
        layoutRoot.findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleGrouping
        )
    }

    private fun goToCombination() {
        layoutRoot.findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleCombination
        )
    }

    private fun goToVoting() {
        layoutRoot.findNavController().navigate(
            R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleVotingStart
        )
    }
}
