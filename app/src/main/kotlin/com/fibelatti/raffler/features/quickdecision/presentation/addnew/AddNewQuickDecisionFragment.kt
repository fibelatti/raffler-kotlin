package com.fibelatti.raffler.features.quickdecision.presentation.addnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.BundleDelegate
import com.fibelatti.raffler.core.platform.base.BaseBottomSheetDialogFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleSelectorAdapter
import kotlinx.android.synthetic.main.fragment_add_custom_raffle_as_quick_decision.*
import javax.inject.Inject

const val ADD_NEW_QUICK_DECISION_REQUEST_CODE = 1
const val ADD_NEW_QUICK_DECISION_SUCCESS = 1

private var Bundle.requestCode by BundleDelegate.Int("REQUEST_CODE")

class AddNewQuickDecisionFragment : BaseBottomSheetDialogFragment() {

    companion object {
        fun bundle() = Bundle().apply {
            this.requestCode = ADD_NEW_QUICK_DECISION_REQUEST_CODE
        }
    }

    private val addNewQuickDecisionViewModel by lazy {
        viewModelFactory.get<AddNewQuickDecisionViewModel>(this)
    }

    @Inject
    lateinit var adapter: CustomRaffleSelectorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        addNewQuickDecisionViewModel.run {
            observe(customRaffles, ::showCustomRaffles)
            observeEvent(updateFeedback) { sendResult() }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_add_custom_raffle_as_quick_decision, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        addNewQuickDecisionViewModel.getAllCustomRaffles()
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withLinearLayoutManager()
            .adapter = adapter
    }

    private fun showCustomRaffles(list: List<CustomRaffleModel>) {
        adapter.apply {
            clickListener = addNewQuickDecisionViewModel::addCustomRaffleAsQuickDecision
            setItems(list)
        }
    }

    private fun sendResult() {
        targetFragment?.onActivityResult(targetRequestCode, ADD_NEW_QUICK_DECISION_SUCCESS, null)
        dismiss()
    }
}
