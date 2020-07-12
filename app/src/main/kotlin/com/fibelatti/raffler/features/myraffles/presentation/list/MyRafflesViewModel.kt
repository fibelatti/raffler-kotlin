package com.fibelatti.raffler.features.myraffles.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.functional.mapCatching
import com.fibelatti.core.functional.onFailure
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyRafflesViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper
) : BaseViewModel() {

    val customRaffles: LiveData<List<CustomRaffleModel>> get() = _customRaffles
    private val _customRaffles = MutableLiveData<List<CustomRaffleModel>>()
    val showHintAndCreateNewLayout: LiveData<Boolean> get() = _showHintAndCreateNewLayout
    private val _showHintAndCreateNewLayout = MutableLiveData<Boolean>()

    fun getAllCustomRaffles() {
        launch {
            customRaffleRepository.getAllCustomRaffles()
                .mapCatching(customRaffleModelMapper::mapList)
                .onSuccess { list ->
                    list.takeIf { it.isNotEmpty() }
                        ?.let(_customRaffles::postValue)
                        ?: _showHintAndCreateNewLayout.postValue(true)
                }.onFailure(::handleError)
        }
    }
}
