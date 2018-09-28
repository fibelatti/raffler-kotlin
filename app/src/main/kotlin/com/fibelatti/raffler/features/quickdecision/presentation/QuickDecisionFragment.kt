package com.fibelatti.raffler.features.quickdecision.presentation

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelFactory
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.exhaustive
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BaseViewType
import com.fibelatti.raffler.core.platform.ItemOffsetDecoration
import com.fibelatti.raffler.features.quickdecision.presentation.adapter.QuickDecisionAdapter
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.layout_hint_container.*
import javax.inject.Inject

class QuickDecisionFragment : BaseFragment() {

    companion object {
        val TAG: String = QuickDecisionFragment::class.java.simpleName

        fun newInstance() = QuickDecisionFragment()
    }

    @Inject
    lateinit var adapter: QuickDecisionAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var sharedView: View

    private val quickDecisionViewModel by lazy {
        viewModelFactory.of<QuickDecisionViewModel>(this@QuickDecisionFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector?.inject(this)
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
            quickDecisionClickListener = { view, quickDecisionModel, color ->
                sharedView = view
                quickDecisionViewModel.getQuickDecisionResult(quickDecisionModel, color)
            }
            colorList = calculateColorGradient(
                getColor(requireContext(), R.color.color_accent),
                getColor(requireContext(), R.color.color_primary),
                steps = dataSet.size - 1
            )
            submitList(dataSet)
        }
    }

    private fun showQuickDecisionResult(state: QuickDecisionViewModel.State.ShowResult) {
        findNavController().navigate(
            R.id.action_fragmentQuickDecision_to_fragmentQuickDecisionResult,
            QuickDecisionResultFragment.bundle(
                ViewCompat.getTransitionName(sharedView),
                state.title,
                state.result,
                state.color
            ),
            NavOptions.Builder()
                .setExitAnim(R.anim.fade_out)
                .setPopExitAnim(R.anim.slide_down)
                .build(),
            FragmentNavigatorExtras(sharedView to ViewCompat.getTransitionName(sharedView))
        )
    }

    private fun setupRecyclerView() {
        recyclerViewItems.addItemDecoration(ItemOffsetDecoration(recyclerViewItems.context, R.dimen.margin_xsmall))
        recyclerViewItems.adapter = adapter
        recyclerViewItems.layoutManager = GridLayoutManager(context, 2)
    }

    private fun calculateColorGradient(startColor: Int, endColor: Int, steps: Int): List<Int> {
        val r1 = Color.red(startColor)
        val g1 = Color.green(startColor)
        val b1 = Color.blue(startColor)

        val r2 = Color.red(endColor)
        val g2 = Color.green(endColor)
        val b2 = Color.blue(endColor)

        val redStep = (r2 - r1) / steps
        val greenStep = (g2 - g1) / steps
        val blueStep = (b2 - b1) / steps

        return (0..steps).map { Color.rgb(r1 + redStep * it, g1 + greenStep * it, b1 + blueStep * it) }
    }
}
