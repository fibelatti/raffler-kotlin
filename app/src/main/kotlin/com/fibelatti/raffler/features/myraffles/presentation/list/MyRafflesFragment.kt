package com.fibelatti.raffler.features.myraffles.presentation.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.gone
import com.fibelatti.core.extension.visible
import com.fibelatti.core.extension.withGridLayoutManager
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.recyclerview.AddNewModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsFragment
import kotlinx.android.synthetic.main.fragment_my_raffles.*
import kotlinx.android.synthetic.main.layout_hint_container.*
import javax.inject.Inject

class MyRafflesFragment @Inject constructor(
    private val customRaffleAdapter: CustomRaffleAdapter
) : BaseFragment(R.layout.fragment_my_raffles) {

    private val myRafflesViewModel by viewModel { viewModelProvider.myRafflesViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()

        myRafflesViewModel.run {
            viewLifecycleOwner.observe(error, ::handleError)
            viewLifecycleOwner.observe(customRaffles, ::showCustomRaffles)
            viewLifecycleOwner.observe(showHintAndCreateNewLayout) { showHintAndCreateNewLayout(it) }
        }

        myRafflesViewModel.getAllCustomRaffles()
    }

    private fun setupLayout() {
        buttonCreateRaffle.setOnClickListener {
            findNavController().navigate(
                R.id.action_fragmentMyRaffles_to_fragmentCreateCustomRaffle,
                CreateCustomRaffleFragment.bundle(),
                CreateCustomRaffleFragment.navOptionsNew()
            )
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withGridLayoutManager(spanCount = 2)
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = customRaffleAdapter
    }

    private fun showCustomRaffles(list: List<CustomRaffleModel>) {
        layoutHintContainer.removeAllViews()
        buttonCreateRaffle.gone()

        val dataSet = listOf(AddNewModel) + list

        customRaffleAdapter.apply {
            addNewClickListener = {
                findNavController().navigate(
                    R.id.action_fragmentMyRaffles_to_fragmentCreateCustomRaffle,
                    CreateCustomRaffleFragment.bundle(),
                    CreateCustomRaffleFragment.navOptionsNew()
                )
            }
            customRaffleClickListener = {
                findNavController().navigate(
                    R.id.action_fragmentMyRaffles_to_fragmentCustomRaffleDetails,
                    CustomRaffleDetailsFragment.bundle(customRaffleId = it)
                )
            }
            colorList = getColorGradientForListSize(
                requireContext(),
                R.color.color_accent,
                R.color.color_primary,
                dataSet.size
            )
            submitList(dataSet)
        }
        recyclerViewItems.visible()
    }

    private fun showHintAndCreateNewLayout(shouldShow: Boolean) {
        if (shouldShow) {
            showDismissibleHint(
                container = layoutHintContainer,
                hintTitle = getString(R.string.my_raffles_hint_title),
                hintMessage = getString(R.string.my_raffles_hint_message)
            )
            buttonCreateRaffle.visible()
            recyclerViewItems.gone()
        } else {
            layoutHintContainer.removeAllViews()
            buttonCreateRaffle.gone()
        }
    }
}
