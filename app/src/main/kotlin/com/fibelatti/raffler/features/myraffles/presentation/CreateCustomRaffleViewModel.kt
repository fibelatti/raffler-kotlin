package com.fibelatti.raffler.features.myraffles.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import javax.inject.Inject

class CreateCustomRaffleViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }
    val showCreateCustomRaffleLayout by lazy { MutableLiveData<Unit>() }

    fun getCustomRaffleById(id: Int) {
        if (id != 0) {
            start {
                inBackground {
                    customRaffleRepository.getCustomRaffleById(id.toLong())
                        .flatMapCatching { customRaffle -> customRaffleModelMapper.map(customRaffle) }
                }.either(::handleError, customRaffle::setValue)
            }
        } else {
            showCreateCustomRaffleLayout.value = Unit
        }
    }
}
