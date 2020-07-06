package com.fibelatti.raffler.features.lottery.presentation

import android.os.Bundle
import android.view.View
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.clearError
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.textAsString
import com.fibelatti.core.extension.visible
import com.fibelatti.core.extension.withGridLayoutManager
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_lottery.*
import javax.inject.Inject

private const val EVEN_SPAN_COUNT = 4
private const val ODD_SPAN_COUNT = 3

class LotteryFragment @Inject constructor(
    private val lotteryAdapter: LotteryAdapter
) : BaseFragment(R.layout.fragment_lottery) {

    private val lotteryViewModel by viewModel { viewModelProvider.lotteryViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()

        viewLifecycleOwner.observe(lotteryViewModel.error, ::handleError)
        viewLifecycleOwner.observe(lotteryViewModel.defaultQuantityAvailable, editTextTotalQuantity::setText)
        viewLifecycleOwner.observe(lotteryViewModel.defaultQuantityToRaffle, editTextRaffleQuantity::setText)
        viewLifecycleOwner.observeEvent(lotteryViewModel.showHint) {
            showDismissibleHint(
                container = layoutHintContainer,
                hintTitle = getString(R.string.hint_quick_tip),
                hintMessage = getString(R.string.lottery_dismissible_hint),
                onHintDismissed = { lotteryViewModel.hintDismissed() }
            )
        }
        viewLifecycleOwner.observe(lotteryViewModel.lotteryNumbers, ::showResults)
        viewLifecycleOwner.observe(lotteryViewModel.quantityAvailableError, ::handleTotalQuantityError)
        viewLifecycleOwner.observe(lotteryViewModel.quantityToRaffleError, ::handleRaffleQuantityError)
    }

    private fun setupLayout() {
        buttonRaffle.setOnClickListener {
            lotteryViewModel.getLotteryNumbers(
                editTextTotalQuantity.textAsString(),
                editTextRaffleQuantity.textAsString()
            )
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = lotteryAdapter
    }

    private fun showResults(results: List<LotteryNumberModel>) {
        val spanCount = when {
            results.size % ODD_SPAN_COUNT == 0 -> ODD_SPAN_COUNT
            results.size % 2 == 0 -> EVEN_SPAN_COUNT
            else -> ODD_SPAN_COUNT
        }

        layoutRoot.hideKeyboard()
        recyclerViewItems.withGridLayoutManager(spanCount).visible()
        lotteryAdapter.submitList(results)
    }

    private fun handleTotalQuantityError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutTotalQuantity.showError(message)
        } else {
            inputLayoutTotalQuantity.clearError()
        }
    }

    private fun handleRaffleQuantityError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutRaffleQuantity.showError(message)
        } else {
            inputLayoutRaffleQuantity.clearError()
        }
    }
}
