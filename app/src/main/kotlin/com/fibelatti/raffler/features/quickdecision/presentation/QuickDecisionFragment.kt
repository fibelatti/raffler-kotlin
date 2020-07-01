package com.fibelatti.raffler.features.quickdecision.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.exhaustive
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withGridLayoutManager
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.core.platform.recyclerview.AddNewModel
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import com.fibelatti.raffler.features.quickdecision.presentation.adapter.QuickDecisionAdapter
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.ADD_NEW_QUICK_DECISION_REQUEST_CODE
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.ADD_NEW_QUICK_DECISION_SUCCESS
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.AddNewQuickDecisionFragment
import kotlinx.android.synthetic.main.fragment_quick_decisions.*
import kotlinx.android.synthetic.main.layout_hint_container.*
import javax.inject.Inject

class QuickDecisionFragment @Inject constructor(
    private val quickDecisionAdapter: QuickDecisionAdapter
) : BaseFragment() {

    private lateinit var sharedView: View

    private val quickDecisionViewModel by viewModel { viewModelProvider.quickDecisionViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quickDecisionViewModel.run {
            error(error, ::handleError)
            observe(state, ::handleState)
            observeEvent(showHint) {
                showDismissibleHint(
                    container = layoutHintContainer,
                    hintTitle = getString(R.string.hint_did_you_know),
                    hintMessage = getString(R.string.quick_decision_dismissible_hint),
                    onHintDismissed = { quickDecisionViewModel.hintDismissed() }
                )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_quick_decisions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        quickDecisionViewModel.getAllQuickDecisions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ADD_NEW_QUICK_DECISION_REQUEST_CODE == requestCode && ADD_NEW_QUICK_DECISION_SUCCESS == resultCode) {
            quickDecisionViewModel.getAllQuickDecisions()
        }
    }

    private fun handleState(state: QuickDecisionViewModel.State) {
        when (state) {
            is QuickDecisionViewModel.State.ShowList -> showQuickDecisions(state)
            is QuickDecisionViewModel.State.ShowResult -> showQuickDecisionResult(state)
        }.exhaustive
    }

    private fun showQuickDecisions(state: QuickDecisionViewModel.State.ShowList) {
        val dataSet = ArrayList<BaseViewType>()
            .apply {
                add(AddNewModel)
                addAll(state.quickDecisions)
            }

        quickDecisionAdapter.apply {
            addNewClickListener = {
                if (state.hasCustomRaffles) {
                    findNavController(layoutRoot).navigate(
                        R.id.action_fragmentQuickDecision_to_dialogFragmentAddNewQuickDecision,
                        AddNewQuickDecisionFragment.bundle()
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
            setItems(dataSet)
        }
    }

    private fun showQuickDecisionResult(state: QuickDecisionViewModel.State.ShowResult) {
        val transitionName = ViewCompat.getTransitionName(sharedView).orEmpty()

        findNavController(layoutRoot).navigate(
            R.id.action_fragmentQuickDecision_to_fragmentQuickDecisionResult,
            QuickDecisionResultFragment.bundle(
                transitionName,
                state.title,
                state.result,
                state.color
            ),
            null,
            FragmentNavigatorExtras(sharedView to transitionName)
        )
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withGridLayoutManager(2)
            .adapter = quickDecisionAdapter
    }
}
