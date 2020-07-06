package com.fibelatti.raffler.features.quickdecision.presentation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.fibelatti.core.android.BundleDelegate
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.customview.ViewOnTouchListener
import kotlinx.android.synthetic.main.fragment_quick_decision_result.*
import javax.inject.Inject

private var Bundle.transitionName by BundleDelegate.String("TRANSITION_NAME")
private var Bundle.title by BundleDelegate.String("TITLE")
private var Bundle.result by BundleDelegate.String("RESULT")
private var Bundle.color by BundleDelegate.Int("COLOR")

class QuickDecisionResultFragment @Inject constructor() : BaseFragment(
    R.layout.fragment_quick_decision_result
) {

    companion object {

        fun bundle(
            transitionName: String,
            title: String,
            result: String,
            color: Int
        ) = Bundle().apply {
            this.transitionName = transitionName
            this.title = title
            this.result = result
            this.color = color
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        arguments?.run {
            layoutQuickDecisionRoot.transitionName = transitionName
            layoutQuickDecisionRoot.setShapeBackgroundColor(color)
            layoutQuickDecisionRoot.setOnTouchListener(ViewOnTouchListener().apply {
                onTopToBottomSwipe = { findNavController().navigateUp() }
            })

            textViewQuickDecisionName.text = title
            textViewButtonDismiss.setOnClickListener { findNavController().navigateUp() }
            textViewResult.text = result
        }
    }
}
