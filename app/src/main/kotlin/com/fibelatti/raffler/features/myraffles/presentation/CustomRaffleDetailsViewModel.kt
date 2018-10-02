package com.fibelatti.raffler.features.myraffles.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import javax.inject.Inject

class CustomRaffleDetailsViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }

    fun getCustomRaffleById(id: Int) {
        start {
            inBackground {
                customRaffleRepository.getCustomRaffleById(id.toLong())
                    .flatMapCatching { customRaffleModelMapper.map(it) }
            }.either(::handleError, customRaffle::setValue)
        }
    }
}
