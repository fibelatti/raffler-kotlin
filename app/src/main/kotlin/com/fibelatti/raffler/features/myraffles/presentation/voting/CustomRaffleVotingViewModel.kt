package com.fibelatti.raffler.features.myraffles.presentation.voting

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.getOrDefaultValue
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleVotingDataSource
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import javax.inject.Inject

private const val MIN_PIN_LENGTH = 4

class CustomRaffleVotingViewModel @Inject constructor(
    private val customRaffleVotingDataSource: CustomRaffleVotingDataSource,
    private val customRaffleVotingModelMapper: CustomRaffleVotingModelMapper,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val ongoingVoting by lazy { MutableLiveEvent<Unit>() }
    val voting by lazy { MutableLiveData<CustomRaffleVotingModel>() }
    val readyToVote by lazy { MutableLiveEvent<Unit>() }
    val results by lazy { MutableLiveData<Map<String, Int>>() }
    val pinError by lazy { MutableLiveEvent<String>() }

    fun checkForOngoingVoting(customRaffle: CustomRaffleModel) {
        startInBackground {
            customRaffleVotingDataSource.getCustomRaffleVoting(customRaffle.id)
                .onSuccess { existingVoting ->
                    existingVoting?.let {
                        ongoingVoting.postEvent(Unit)
                        voting.postValue(customRaffleVotingModelMapper.map(it))
                    }
                }
        }
    }

    fun setupNewVoting(pin: String, customRaffle: CustomRaffleModel) {
        startInBackground {
            if (pin.length < MIN_PIN_LENGTH) {
                pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_invalid))
            } else {
                customRaffleVotingModelMapper.map(pin.toInt(), customRaffle)
                    .let(customRaffleVotingModelMapper::mapReverse)
                    .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                    .onSuccess { readyToVote.postEvent(Unit) }
            }
        }
    }

    fun resumeVoting(pin: String) {
        withVoting {
            when {
                pin.isBlank() -> pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_invalid))
                it.pin == pin.toInt() -> readyToVote.postEvent(Unit)
                else -> {
                    pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
                }
            }
        }
    }

    fun vote(description: String) {
        withVoting { currentVoting ->
            startInBackground {
                currentVoting.copy(
                    totalVotes = currentVoting.totalVotes.inc(),
                    votes = currentVoting.votes.toMutableMap().apply {
                        set(description, currentVoting.votes.getOrDefaultValue(description, 0).inc())
                    }
                ).let(customRaffleVotingModelMapper::mapReverse)
                    .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                    .onSuccess { readyToVote.postEvent(Unit) }
            }
        }
    }

    fun getVotingResults(pin: String) {
        withVoting {
            if (it.pin == pin.toInt()) {
                results.postValue(it.votes)
            } else {
                pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
            }
        }
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        voting.value?.let(body)
    }
}
