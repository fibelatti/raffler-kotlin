package com.fibelatti.raffler.features.myraffles.presentation.combination

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
import com.fibelatti.core.extension.textAsString
import com.fibelatti.core.extension.visible
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.combineLatest
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedAdapter
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleSelector
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleSelectorDelegate
import kotlinx.android.synthetic.main.fragment_custom_raffle_combination.*
import javax.inject.Inject

class CustomRaffleCombinationFragment @Inject constructor(
    private val customRaffleDraftedAdapter: CustomRaffleDraftedAdapter
) : BaseFragment(R.layout.fragment_custom_raffle_combination),
    CustomRaffleSelector by CustomRaffleSelectorDelegate() {

    private lateinit var secondCustomRaffle: CustomRaffleModel

    private val customRaffleDetailsViewModel by activityViewModel { viewModelProvider.customRaffleDetailsViewModel() }
    private val customRaffleCombinationViewModel by viewModel { viewModelProvider.customRaffleCombinationViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewLifecycleOwner.observe(customRaffleDetailsViewModel.customRaffle, ::setupLayout)
        viewLifecycleOwner.observe(customRaffleCombinationViewModel.error, ::handleError)
        viewLifecycleOwner.observe(
            customRaffleCombinationViewModel.otherCustomRaffles
                .combineLatest(customRaffleDetailsViewModel.customRaffle),
            ::showSelector
        )
        viewLifecycleOwner.observeEvent(
            customRaffleCombinationViewModel.quantityError,
            ::handleQuantityError
        )
        viewLifecycleOwner.observe(customRaffleCombinationViewModel.pairs, ::handlePairs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    private fun setupLayout(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setNavigateUp(R.drawable.ic_close) {
            findNavController().navigateUp()
        }
        layoutTitle.setTitle(
            getString(R.string.custom_raffle_combination_title, customRaffleModel.description)
        )

        textViewFirstCustomRaffleDescription.text = customRaffleModel.description
        textViewFirstCustomRaffleItems.text = getString(
            R.string.my_raffles_items_quantity,
            customRaffleModel.includedItems.size
        )

        layoutSecondCustomRaffle.setOnClickListener {
            customRaffleCombinationViewModel.getCustomRafflesToCombineWith()
        }
        textViewSecondCustomRaffleDescription.setText(R.string.custom_raffle_combination_hint)

        buttonRaffle.isEnabled = false
        buttonRaffle.setOnClickListener {
            customRaffleCombinationViewModel.getCombinations(
                customRaffleModel,
                secondCustomRaffle,
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
            inputLayoutQuantity.showError(message)
        } else {
            inputLayoutQuantity.clearError()
        }
    }

    private fun showSelector(raffles: Pair<List<CustomRaffleModel>, CustomRaffleModel>) {
        showCustomRaffleSelector(
            context = requireContext(),
            title = getString(
                R.string.custom_raffle_combination_selector_title,
                raffles.second.description
            ),
            customRaffles = raffles.first,
            customRaffleClickListener = {
                secondCustomRaffle = it

                textViewSecondCustomRaffleDescription.text = it.description
                textViewSecondCustomRaffleItems.visible()
                textViewSecondCustomRaffleItems.text = getString(
                    R.string.my_raffles_items_quantity,
                    it.items.size
                )

                buttonRaffle.isEnabled = true
            }
        )
    }

    private fun handlePairs(pairs: List<CustomRaffleDraftedModel>) {
        layoutRoot.hideKeyboard()
        customRaffleDraftedAdapter.run {
            colorList = getColorGradientForListSize(
                requireContext(),
                R.color.color_primary,
                R.color.color_secondary,
                pairs.size
            )
            submitList(pairs)
        }
    }
}
