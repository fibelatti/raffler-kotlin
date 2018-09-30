package com.fibelatti.raffler.features.quickdecision.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.exhaustive
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BaseViewType
import com.fibelatti.raffler.features.myraffles.presentation.CreateRaffleActivity
import com.fibelatti.raffler.features.quickdecision.presentation.adapter.QuickDecisionAdapter
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.layout_hint_container.*
import javax.inject.Inject

class QuickDecisionFragment : BaseFragment() {

    @Inject
    lateinit var adapter: QuickDecisionAdapter

    private lateinit var sharedView: View

    private val quickDecisionViewModel by lazy {
        viewModelFactory.of<QuickDecisionViewModel>(this@QuickDecisionFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        quickDecisionViewModel.run {
            error(error) { handleError(it) }
            observe(state) { handleState(it) }
            getAllQuickDecisions()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        showDismissibleHint(
            container = layoutHintContainer,
            hintTitle = getString(R.string.hint_did_you_know),
            hintMessage = getString(R.string.quick_decision_dismissible_hint)
        )
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
                add(AddNewModel())
                addAll(state.quickDecisions)
            }

        adapter.apply {
            addNewClickListener = {
                findNavController(layoutRoot).navigate(
                    R.id.action_fragmentQuickDecision_to_activityCreateRaffle,
                    CreateRaffleActivity.bundle(addAsShortcut = true)
                )
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
            NavOptions.Builder()
                .setExitAnim(R.anim.fade_out)
                .setPopExitAnim(R.anim.slide_down)
                .build(),
            FragmentNavigatorExtras(sharedView to transitionName)
        )
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
        recyclerViewItems.layoutManager = GridLayoutManager(context, 2)
        recyclerViewItems.adapter = adapter
    }
}
