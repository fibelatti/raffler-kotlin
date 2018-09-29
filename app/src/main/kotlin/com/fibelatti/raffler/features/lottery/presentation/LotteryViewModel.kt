package com.fibelatti.raffler.features.lottery.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.flatMap
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.randomize.Randomize
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class LotteryViewModel @Inject constructor(
    private val randomize: Randomize,
    private val lotteryNumberModelMapper: LotteryNumberModelMapper,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val lotteryNumbers by lazy { MutableLiveData<List<LotteryNumberModel>>() }
    val totalQuantityError by lazy { MutableLiveData<String>() }
    val raffleQuantityError by lazy { MutableLiveData<String>() }

    fun getLotteryNumbers(totalQuantity: String, raffleQuantity: String) {
        launch {
            when {
                totalQuantity.isBlank() || !totalQuantity.isInt() -> {
                    totalQuantityError.value = resourceProvider.getString(R.string.lottery_quantity_validation_error)
                }
                raffleQuantity.isBlank() || !raffleQuantity.isInt() -> {
                    totalQuantityError.value = String.empty()
                    raffleQuantityError.value = resourceProvider.getString(R.string.lottery_quantity_validation_error)
                }
                else -> {
                    totalQuantityError.value = String.empty()
                    raffleQuantityError.value = String.empty()

                    inBackground {
                        randomize(Randomize.Params(totalQuantity.toInt(), raffleQuantity.toInt()))
                            .flatMap { runCatching { lotteryNumberModelMapper.map(it) } }
                    }.either(
                        { error.value = it },
                        { lotteryNumbers.value = it }
                    )
                }
            }
        }
    }
}
