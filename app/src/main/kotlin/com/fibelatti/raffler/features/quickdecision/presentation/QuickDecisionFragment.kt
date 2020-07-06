package com.fibelatti.raffler.features.quickdecision.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.withGridLayoutManager
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.extension.observeNavigationResult
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.recyclerview.AddNewModel
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import com.fibelatti.raffler.features.quickdecision.presentation.adapter.QuickDecisionAdapter
import kotlinx.android.synthetic.main.fragment_quick_decisions.*
import kotlinx.android.synthetic.main.layout_hint_container.*
import javax.inject.Inject

class QuickDecisionFragment @Inject constructor(
    private val quickDecisionAdapter: QuickDecisionAdapter
) : BaseFragment(R.layout.fragment_quick_decisions) {

    private lateinit var sharedView: View

    private val quickDecisionViewModel by viewModel { viewModelProvider.quickDecisionViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.observe(quickDecisionViewModel.error, ::handleError)
        viewLifecycleOwner.observe(quickDecisionViewModel.quickDecisionList, ::showQuickDecisions)
        viewLifecycleOwner.observeEvent(quickDecisionViewModel.quickDecisionResult, ::showQuickDecisionResult)
        viewLifecycleOwner.observeEvent(quickDecisionViewModel.showHint) {
            showDismissibleHint(
                container = layoutHintContainer,
                hintTitle = getString(R.string.hint_did_you_know),
                hintMessage = getString(R.string.quick_decision_dismissible_hint),
                onHintDismissed = { quickDecisionViewModel.hintDismissed() }
            )
        }

        observeNavigationResult<Boolean> { quickDecisionViewModel.getAllQuickDecisions() }

        quickDecisionViewModel.getAllQuickDecisions()
    }

    private fun showQuickDecisions(quickDecisionList: QuickDecisionViewModel.QuickDecisionList) {
        val dataSet: List<BaseViewType> = listOf(AddNewModel) + quickDecisionList.quickDecisions

        quickDecisionAdapter.apply {
            addNewClickListener = {
                if (quickDecisionList.hasCustomRaffles) {
                    findNavController(layoutRoot).navigate(
                        R.id.action_fragmentQuickDecision_to_dialogFragmentAddNewQuickDecision
                    )
                } else {
                    findNavController(layoutRoot).navigate(
                        R.id.action_fragmentQuickDecision_to_fragmentCreateCustomRaffle,
                        CreateCustomRaffleFragment.bundle(addAsShortcut = true),
                        CreateCustomRaffleFragment.navOptionsNew()
                    )
                }
            }
            quickDecisionClickListener = { view, quickDecisionModel, color ->
                sharedView = view
                quickDecisionViewModel.getQuickDecisionResult(quickDecisionModel, color)
            }
            colorList = getColorGradientForListSize(
                requireContext(),
                R.color.color_accent,
                R.color.color_primary,
                dataSet.size
            )
            submitList(dataSet)
        }
    }

    private fun showQuickDecisionResult(quickDecisionResult: QuickDecisionViewModel.QuickDecisionResult) {
        val transitionName = ViewCompat.getTransitionName(sharedView).orEmpty()

        findNavController().navigate(
            R.id.action_fragmentQuickDecision_to_fragmentQuickDecisionResult,
            QuickDecisionResultFragment.bundle(
                transitionName,
                quickDecisionResult.title,
                quickDecisionResult.result,
                quickDecisionResult.color
            ),
            null,
            FragmentNavigatorExtras(sharedView to transitionName)
        )
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withGridLayoutManager(spanCount = 2)
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = quickDecisionAdapter
    }
}
