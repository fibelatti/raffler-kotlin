package com.fibelatti.raffler.features.lottery.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import javax.inject.Inject

class LotteryViewModel @Inject constructor(
    private val lotteryNumberModelMapper: LotteryNumberModelMapper,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val lotteryNumbers by lazy { MutableLiveData<List<LotteryNumberModel>>() }
    val totalQuantityError by lazy { MutableLiveData<String>() }
    val raffleQuantityError by lazy { MutableLiveData<String>() }

    fun getLotteryNumbers(totalQuantity: String, raffleQuantity: String) {
        start {
            inBackground {
                validateData(totalQuantity, raffleQuantity) { totalQty, raffleQty ->
                    (1..totalQty).shuffled()
                        .take(raffleQty)
                        .map(lotteryNumberModelMapper::map)
                        .let(lotteryNumbers::postValue)
                }
            }
        }
    }

    private fun validateData(
        totalQuantity: String,
        raffleQuantity: String,
        ifValid: (totalQuantity: Int, raffleQuantity: Int) -> Unit
    ) {
        when {
            totalQuantity.isBlank() || !totalQuantity.isInt() -> {
                totalQuantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            raffleQuantity.isBlank() || !raffleQuantity.isInt() -> {
                totalQuantityError.postValue(String.empty())
                raffleQuantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            else -> {
                totalQuantityError.postValue(String.empty())
                raffleQuantityError.postValue(String.empty())

                ifValid(totalQuantity.toInt(), raffleQuantity.toInt())
            }
        }
    }
}
