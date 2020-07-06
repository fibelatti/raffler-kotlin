package com.fibelatti.raffler.features.myraffles.presentation.randomwinners

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.clearError
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.snackbar
import com.fibelatti.core.extension.textAsString
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedAdapter
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_custom_raffle_random_winners.*
import javax.inject.Inject

class CustomRaffleRandomWinnersFragment @Inject constructor(
    private val customRaffleDraftedAdapter: CustomRaffleDraftedAdapter
) : BaseFragment(R.layout.fragment_custom_raffle_random_winners) {

    private val customRaffleDetailsViewModel by activityViewModel {
        viewModelProvider.customRaffleDetailsViewModel()
    }
    private val randomWinnersViewModel by viewModel {
        viewModelProvider.customRaffleRandomWinnersViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.observe(customRaffleDetailsViewModel.customRaffle, ::setupLayout)
        viewLifecycleOwner.observeEvent(customRaffleDetailsViewModel.itemsRemaining) {
            layoutRoot.snackbar(
                resources.getQuantityString(R.plurals.custom_raffle_roulette_hint_items_remaining, it, it),
                duration = Snackbar.LENGTH_LONG
            )

            buttonRaffle.isEnabled = it == 1
        }

        viewLifecycleOwner.observe(randomWinnersViewModel.error, ::handleError)
        viewLifecycleOwner.observe(randomWinnersViewModel.quantityError, ::handleQuantityError)
        viewLifecycleOwner.observe(randomWinnersViewModel.randomWinners, ::handleWinners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    private fun setupLayout(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setTitle(R.string.custom_raffle_details_mode_random_winners)
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }

        buttonRaffle.setOnClickListener {
            randomWinnersViewModel.getRandomWinners(
                customRaffleModel.includedItems,
                editTextTotalQuantity.textAsString()
            )
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withLinearLayoutManager()
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = customRaffleDraftedAdapter
    }

    private fun handleQuantityError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutWinnersQuantity.showError(message)
        } else {
            inputLayoutWinnersQuantity.clearError()
        }
    }

    private fun handleWinners(winners: List<CustomRaffleDraftedModel>) {
        customRaffleDetailsViewModel.itemRaffled()
        layoutRoot.hideKeyboard()
        customRaffleDraftedAdapter.run {
            colorList = getColorGradientForListSize(
                requireContext(),
                R.color.color_accent,
                R.color.color_primary,
                winners.size
            )
            submitList(winners)
        }
    }
}
