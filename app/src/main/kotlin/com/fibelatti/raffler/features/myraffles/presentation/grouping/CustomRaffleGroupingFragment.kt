package com.fibelatti.raffler.features.myraffles.presentation.grouping

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.clearError
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.textAsString
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedAdapter
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleDraftedModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_grouping.*
import javax.inject.Inject

class CustomRaffleGroupingFragment @Inject constructor(
    private val customRaffleDraftedAdapter: CustomRaffleDraftedAdapter
) : BaseFragment(R.layout.fragment_custom_raffle_grouping) {

    private val customRaffleDetailsViewModel by activityViewModel { viewModelProvider.customRaffleDetailsViewModel() }
    private val customRaffleGroupingViewModel by viewModel { viewModelProvider.customRaffleGroupingViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.observe(customRaffleDetailsViewModel.customRaffle, ::setupLayout)
        customRaffleGroupingViewModel.run {
            viewLifecycleOwner.observe(error, ::handleError)
            viewLifecycleOwner.observe(quantityError, ::handleQuantityError)
            viewLifecycleOwner.observe(groups, ::handleGroups)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    private fun setupLayout(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setTitle(R.string.custom_raffle_details_mode_grouping)
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }

        buttonRaffle.setOnClickListener {
            if (radioButtonByGroup.isChecked) {
                customRaffleGroupingViewModel.getGroupsByQuantity(
                    customRaffleModel.includedItems,
                    editTextTotalQuantity.textAsString()
                )
            } else {
                customRaffleGroupingViewModel.getGroupsByItemQuantity(
                    customRaffleModel.includedItems,
                    editTextTotalQuantity.textAsString()
                )
            }
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

    private fun handleGroups(winners: List<CustomRaffleDraftedModel>) {
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
