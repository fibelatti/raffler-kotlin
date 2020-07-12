package com.fibelatti.raffler.features.quickdecision.presentation.addnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setNavigationResult
import com.fibelatti.raffler.core.platform.base.BaseBottomSheetDialogFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleSelectorAdapter
import kotlinx.android.synthetic.main.fragment_add_custom_raffle_as_quick_decision.*
import javax.inject.Inject

class AddNewQuickDecisionFragment @Inject constructor(
    private val customRaffleSelectorAdapter: CustomRaffleSelectorAdapter
) : BaseBottomSheetDialogFragment() {

    private val addNewQuickDecisionViewModel by viewModel {
        viewModelProvider.addNewQuickDecisionViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_custom_raffle_as_quick_decision, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.observe(addNewQuickDecisionViewModel.customRaffles, ::showCustomRaffles)
        viewLifecycleOwner.observeEvent(addNewQuickDecisionViewModel.customRaffleAdded) { sendResult() }

        addNewQuickDecisionViewModel.getAllCustomRaffles()
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withLinearLayoutManager()
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = customRaffleSelectorAdapter
    }

    private fun showCustomRaffles(list: List<CustomRaffleModel>) {
        customRaffleSelectorAdapter.apply {
            clickListener = addNewQuickDecisionViewModel::addCustomRaffleAsQuickDecision
            submitList(list)
        }
    }

    private fun sendResult() {
        setNavigationResult(result = true)
        dismiss()
    }
}
