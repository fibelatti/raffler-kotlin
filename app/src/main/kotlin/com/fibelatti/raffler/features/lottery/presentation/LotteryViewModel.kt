package com.fibelatti.raffler.features.lottery.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.isInt
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class LotteryViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val lotteryNumberModelMapper: LotteryNumberModelMapper,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val defaultQuantityAvailable by lazy { MutableLiveData<String>() }
    val defaultQuantityToRaffle by lazy { MutableLiveData<String>() }
    val lotteryNumbers by lazy { MutableLiveData<List<LotteryNumberModel>>() }
    val totalQuantityError by lazy { MutableLiveData<String>() }
    val raffleQuantityError by lazy { MutableLiveData<String>() }

    init {
        getDefaults()
    }

    fun getLotteryNumbers(quantityAvailable: String, quantityToRaffle: String) {
        start {
            inBackground {
                validateData(quantityAvailable, quantityToRaffle) { totalQty, raffleQty ->
                    (1..totalQty).shuffled()
                        .take(raffleQty)
                        .map(lotteryNumberModelMapper::map)
                        .let(lotteryNumbers::postValue)
                }
            }
        }
    }

    private fun getDefaults() {
        start {
            inBackground { preferencesRepository.getPreferences() }
                .onSuccess {
                    defaultQuantityAvailable.value = it.lotteryDefaultQuantityAvailable
                    defaultQuantityToRaffle.value = it.lotteryDefaultQuantityToRaffle
                }
        }
    }

    private fun validateData(
        quantityAvailable: String,
        quantityToRaffle: String,
        ifValid: (quantityAvailable: Int, quantityToRaffle: Int) -> Unit
    ) {
        when {
            quantityAvailable.isBlank() || !quantityAvailable.isInt() -> {
                totalQuantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            quantityToRaffle.isBlank() || !quantityToRaffle.isInt() -> {
                totalQuantityError.postValue(String.empty())
                raffleQuantityError.postValue(resourceProvider.getString(R.string.lottery_quantity_validation_error))
            }
            else -> {
                totalQuantityError.postValue(String.empty())
                raffleQuantityError.postValue(String.empty())

                ifValid(quantityAvailable.toInt(), quantityToRaffle.toInt())
            }
        }
    }
}
