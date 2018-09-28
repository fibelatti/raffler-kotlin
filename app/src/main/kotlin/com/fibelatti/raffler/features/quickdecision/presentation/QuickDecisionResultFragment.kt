package com.fibelatti.raffler.features.quickdecision.presentation

import android.os.Bundle
import android.support.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BundleDelegate
import kotlinx.android.synthetic.main.fragment_quick_decision_result.*

var Bundle.transitionName by BundleDelegate.String("TRANSITION_NAME")
var Bundle.title by BundleDelegate.String("TITLE")
var Bundle.result by BundleDelegate.String("RESULT")
var Bundle.color by BundleDelegate.Int("COLOR")

class QuickDecisionResultFragment
    : BaseFragment() {

    companion object {
        val TAG: String = QuickDecisionResultFragment::class.java.simpleName

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_quick_decision_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        arguments?.run {
            layoutQuickDecisionRoot.transitionName = transitionName
            layoutQuickDecisionRoot.setShapeBackgroundColor(color)
            textViewQuickDecisionName.text = title
            textViewButtonDismiss.setOnClickListener { this@QuickDecisionResultFragment.findNavController().navigateUp() }
            textViewResult.text = result
        }
    }
}
