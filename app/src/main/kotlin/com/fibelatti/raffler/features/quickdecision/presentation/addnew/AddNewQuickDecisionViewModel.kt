package com.fibelatti.raffler.features.quickdecision.presentation.addnew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.functional.mapCatching
import com.fibelatti.core.functional.onFailure
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionModelMapper
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddNewQuickDecisionViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val quickDecisionModelMapper: QuickDecisionModelMapper
) : BaseViewModel() {

    val customRaffles: LiveData<List<CustomRaffleModel>> get() = _customRaffles
    private val _customRaffles = MutableLiveData<List<CustomRaffleModel>>()
    val customRaffleAdded: LiveEvent<Unit> get() = _customRaffleAdded
    private val _customRaffleAdded = MutableLiveEvent<Unit>()

    fun getAllCustomRaffles() {
        launch {
            customRaffleRepository.getAllCustomRaffles()
                .mapCatching(customRaffleModelMapper::mapList)
                .onSuccess(_customRaffles::postValue)
                .onFailure(::handleError)
        }
    }

    fun addCustomRaffleAsQuickDecision(customRaffleModel: CustomRaffleModel) {
        launch {
            quickDecisionRepository.addQuickDecisions(quickDecisionModelMapper.map(customRaffleModel))
                .onSuccess { _customRaffleAdded.postEvent(Unit) }
        }
    }
}
