package com.fibelatti.raffler.features.quickdecision.presentation

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.fibelatti.core.android.BundleDelegate
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.customview.ViewOnTouchListener
import kotlinx.android.synthetic.main.fragment_quick_decision_result.*
import javax.inject.Inject

private var Bundle.transitionName by BundleDelegate.String("TRANSITION_NAME")
private var Bundle.color by BundleDelegate.Int("COLOR")
private var Bundle.quickDecision by BundleDelegate.Parcelable<QuickDecisionModel>("QUICK_DECISION")

class QuickDecisionResultFragment @Inject constructor() : BaseFragment(
    R.layout.fragment_quick_decision_result
) {

    companion object {

        fun bundle(
            transitionName: String,
            color: Int,
            quickDecisionModel: QuickDecisionModel
        ) = Bundle().apply {
            this.transitionName = transitionName
            this.color = color
            this.quickDecision = quickDecisionModel
        }
    }

    private val quickDecisionResultViewModel by viewModel { viewModelProvider.quickDecisionResultViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        viewLifecycleOwner.observeEvent(quickDecisionResultViewModel.quickDecisionResult, ::showQuickDecisionResult)
        arguments?.quickDecision?.let(quickDecisionResultViewModel::getQuickDecisionResult)
    }

    private fun setupLayout() {
        arguments?.color?.let(layoutQuickDecisionRoot::setShapeBackgroundColor)
        layoutQuickDecisionRoot.transitionName = arguments?.transitionName
        layoutQuickDecisionRoot.setOnTouchListener(ViewOnTouchListener().apply {
            onTopToBottomSwipe = { findNavController().navigateUp() }
        })

        val onClickListener = View.OnClickListener {
            arguments?.quickDecision?.let(quickDecisionResultViewModel::getQuickDecisionResult)
        }
        textViewResult.setOnClickListener(onClickListener)
        textViewQuickDecisionReroll.setOnClickListener(onClickListener)
        textViewButtonDismiss.setOnClickListener { findNavController().navigateUp() }
    }

    private fun showQuickDecisionResult(quickDecisionResult: QuickDecisionResultViewModel.QuickDecisionResult) {
        textViewQuickDecisionName.text = quickDecisionResult.title
        textViewResult.text = quickDecisionResult.result

        val anim: Animation = AlphaAnimation(0F, 1F).apply {
            duration = textViewResult.resources.getInteger(R.integer.anim_time_default).toLong()
            repeatMode = Animation.REVERSE
        }
        textViewResult.startAnimation(anim)
    }
}
