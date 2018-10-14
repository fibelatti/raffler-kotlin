package com.fibelatti.raffler.features.quickdecision.presentation.addnew

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.functional.flatMapCatching
import com.fibelatti.raffler.core.functional.onFailure
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.fibelatti.raffler.features.quickdecision.presentation.CustomRaffleToQuickDecisionMapper
import javax.inject.Inject

class AddNewQuickDecisionViewModel @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val quickDecisionRepository: QuickDecisionRepository,
    private val customRaffleModelMapper: CustomRaffleModelMapper,
    private val customRaffleToQuickDecisionMapper: CustomRaffleToQuickDecisionMapper,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val customRaffles by lazy { MutableLiveData<List<CustomRaffleModel>>() }
    val updateFeedback by lazy { MutableLiveEvent<Unit>() }

    fun getAllCustomRaffles() {
        startInBackground {
            customRaffleRepository.getAllCustomRaffles()
                .flatMapCatching { it.map(customRaffleModelMapper::map) }
                .onSuccess(customRaffles::postValue)
                .onFailure(::handleError)
        }
    }

    fun addCustomRaffleAsQuickDecision(customRaffleModel: CustomRaffleModel) {
        startInBackground {
            quickDecisionRepository.addQuickDecisions(listOf(customRaffleToQuickDecisionMapper.map(customRaffleModel)))
                .onSuccess { updateFeedback.postEvent(Unit) }
        }
    }
}
