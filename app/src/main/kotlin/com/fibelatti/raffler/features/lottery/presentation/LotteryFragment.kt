package com.fibelatti.raffler.features.lottery.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.clearError
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.showError
import com.fibelatti.raffler.core.extension.textAsString
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_lottery.*
import javax.inject.Inject

private const val EVEN_SPAN_COUNT = 4
private const val ODD_SPAN_COUNT = 3

class LotteryFragment : BaseFragment() {

    @Inject
    lateinit var adapter: LotteryAdapter

    private val lotteryViewModel by lazy { viewModelFactory.get<LotteryViewModel>(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        lotteryViewModel.run {
            error(error, ::handleError)
            observe(defaultQuantityAvailable) { editTextTotalQuantity.setText(it) }
            observe(defaultQuantityToRaffle) { editTextRaffleQuantity.setText(it) }
            observeEvent(showHint) {
                showDismissibleHint(
                    container = layoutHintContainer,
                    hintTitle = getString(R.string.hint_quick_tip),
                    hintMessage = getString(R.string.lottery_dismissible_hint),
                    onHintDismissed = { lotteryViewModel.hintDismissed() }
                )
            }
            observe(lotteryNumbers, ::showResults)
            observe(quantityAvailableError, ::handleTotalQuantityError)
            observe(quantityToRaffleError, ::handleRaffleQuantityError)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_lottery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
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
        recyclerViewItems.withDefaultDecoration()
        recyclerViewItems.adapter = adapter
    }

    private fun showResults(results: List<LotteryNumberModel>) {
        val spanCount = when {
            results.size % ODD_SPAN_COUNT == 0 -> ODD_SPAN_COUNT
            results.size % 2 == 0 -> EVEN_SPAN_COUNT
            else -> ODD_SPAN_COUNT
        }

        layoutRoot.hideKeyboard()

        recyclerViewItems.layoutManager = GridLayoutManager(context, spanCount)
        recyclerViewItems.visible()
        adapter.setItems(results)
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
