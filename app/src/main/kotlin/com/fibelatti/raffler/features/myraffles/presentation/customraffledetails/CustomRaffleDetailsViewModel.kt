package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class CustomRaffleDetailsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val rouletteMusicEnabled by lazy { MutableLiveData<Boolean>() }
    val customRaffle by lazy { MutableLiveData<CustomRaffleModel>() }
    val preparedRaffle by lazy { MutableLiveData<CustomRaffleModel>() }

    init {
        getRouletteMusicEnabled()
    }

    fun getCustomRaffleById(id: Int) {
        start {
            inBackground {
                customRaffleRepository.getCustomRaffleById(id.toLong())
                    .flatMapCatching { customRaffleModelMapper.map(it) }
            }.either(::handleError) {
                customRaffle.value = it
                preparedRaffle.value = it
            }
        }
    }

    fun updateItemSelection(customRaffleItemModel: CustomRaffleItemModel, isSelected: Boolean) {
        preparedRaffle.value = customRaffle.value?.apply {
            if (isSelected) {
                items.add(customRaffleItemModel)
            } else {
                items.remove(customRaffleItemModel)
            }
        }
    }

    private fun getRouletteMusicEnabled() {
        start {
            rouletteMusicEnabled.value = inBackground {
                preferencesRepository.getRouletteMusicEnabled()
            }
        }
    }
}
