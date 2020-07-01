package com.fibelatti.raffler.features.myraffles.presentation.combination

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
import com.fibelatti.raffler.core.extension.textAsString
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
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
): BaseFragment(),
    CustomRaffleSelector by CustomRaffleSelectorDelegate() {

    private lateinit var secondCustomRaffle: CustomRaffleModel

    private val customRaffleDetailsViewModel by activityViewModel { viewModelProvider.customRaffleDetailsViewModel() }
    private val customRaffleCombinationViewModel by viewModel { viewModelProvider.customRaffleCombinationViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customRaffleCombinationViewModel.run {
            error(error, ::handleError)
            observe(otherCustomRaffles, ::showSelector)
            observeEvent(quantityError, ::handleQuantityError)
            observe(pairs, ::handlePairs)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_combination, container, false)

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
        layoutTitle.setNavigateUp(R.drawable.ic_close) { layoutRoot.findNavController().navigateUp() }

        customRaffleDetailsViewModel.customRaffle.value?.let { raffle ->
            layoutTitle.setTitle(getString(R.string.custom_raffle_combination_title, raffle.description))

            textViewFirstCustomRaffleDescription.text = raffle.description
            textViewFirstCustomRaffleItems.text = getString(R.string.my_raffles_items_quantity, raffle.includedItems.size)

            layoutSecondCustomRaffle.setOnClickListener {
                customRaffleCombinationViewModel.getCustomRafflesToCombineWith(raffle)
            }
            textViewSecondCustomRaffleDescription.setText(R.string.custom_raffle_combination_hint)

            buttonRaffle.isEnabled = false
            buttonRaffle.setOnClickListener {
                customRaffleCombinationViewModel.getPairs(
                    raffle,
                    secondCustomRaffle,
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
            inputLayoutQuantity.showError(message)
        } else {
            inputLayoutQuantity.clearError()
        }
    }

    private fun showSelector(otherCustomRaffles: List<CustomRaffleModel>) {
        showCustomRaffleSelector(
            context = requireContext(),
            title = getString(
                R.string.custom_raffle_combination_selector_title,
                customRaffleDetailsViewModel.customRaffle.value?.description
            ),
            customRaffles = otherCustomRaffles,
            customRaffleClickListener = {
                secondCustomRaffle = it

                textViewSecondCustomRaffleDescription.text = it.description
                textViewSecondCustomRaffleItems.visible()
                textViewSecondCustomRaffleItems.text = getString(R.string.my_raffles_items_quantity, it.items.size)

                buttonRaffle.isEnabled = true
            }
        )
    }

    private fun handlePairs(pairs: List<CustomRaffleDraftedModel>) {
        layoutRoot.hideKeyboard()
        customRaffleDraftedAdapter.run {
            colorList = getColorGradientForListSize(
                requireContext(),
                R.color.color_accent,
                R.color.color_primary,
                pairs.size
            )
            setItems(pairs)
        }
    }
}
