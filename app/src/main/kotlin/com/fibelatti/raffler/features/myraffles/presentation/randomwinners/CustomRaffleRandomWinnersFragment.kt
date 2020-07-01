package com.fibelatti.raffler.features.myraffles.presentation.randomwinners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.clearError
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.showError
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.extension.textAsString
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedAdapter
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_custom_raffle_random_winners.*
import javax.inject.Inject

class CustomRaffleRandomWinnersFragment @Inject constructor(
    private val customRaffleDraftedAdapter: CustomRaffleDraftedAdapter
) : BaseFragment() {

    private val customRaffleDetailsViewModel by activityViewModel {
        viewModelProvider.customRaffleDetailsViewModel()
    }
    private val randomWinnersViewModel by viewModel {
        viewModelProvider.customRaffleRandomWinnersViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customRaffleDetailsViewModel.run {
            observeEvent(itemsRemaining) {
                layoutRoot.snackbar(
                    resources.getQuantityString(R.plurals.custom_raffle_roulette_hint_items_remaining, it, it),
                    duration = Snackbar.LENGTH_LONG
                )

                if (it == 1) buttonRaffle.isEnabled = false
            }
        }

        randomWinnersViewModel.run {
            error(error, ::handleError)
            observe(quantityError, ::handleQuantityError)
            observe(randomWinners, ::handleWinners)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_random_winners, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.custom_raffle_details_mode_random_winners)
        layoutTitle.setNavigateUp(R.drawable.ic_close) { layoutRoot.findNavController().navigateUp() }

        buttonRaffle.setOnClickListener {
            customRaffleDetailsViewModel.customRaffle.value?.let { raffle ->
                randomWinnersViewModel.getRandomWinners(
                    raffle.includedItems,
                    editTextTotalQuantity.textAsString()
                )
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withLinearLayoutManager()
            .withDefaultDecoration()
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
            setItems(winners)
        }
    }
}
