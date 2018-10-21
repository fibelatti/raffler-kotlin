package com.fibelatti.raffler.features.myraffles.presentation.voting

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.getOrDefaultValue
import com.fibelatti.raffler.core.functional.onSuccess
import com.fibelatti.raffler.core.platform.MutableLiveEvent
import com.fibelatti.raffler.core.platform.base.BaseViewModel
import com.fibelatti.raffler.core.platform.base.BaseViewType
import com.fibelatti.raffler.core.platform.postEvent
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.FormatVotingResults
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleVotingDataSource
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import javax.inject.Inject

private const val MIN_PIN_LENGTH = 4

class CustomRaffleVotingViewModel @Inject constructor(
    private val customRaffleVotingDataSource: CustomRaffleVotingDataSource,
    private val customRaffleVotingModelMapper: CustomRaffleVotingModelMapper,
    private val formatVotingResults: FormatVotingResults,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val ongoingVoting by lazy { MutableLiveEvent<Unit>() }
    val voting by lazy { MutableLiveData<CustomRaffleVotingModel>() }
    val readyToVote by lazy { MutableLiveEvent<Unit>() }
    val results by lazy { MutableLiveEvent<List<BaseViewType>>() }
    val pinError by lazy { MutableLiveEvent<String>() }
    val showTieBreaker by lazy { MutableLiveEvent<Unit>() }
    val showRandomDecision by lazy { MutableLiveEvent<CustomRaffleModel>() }

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
                val newVoting = customRaffleVotingModelMapper.map(pin.toInt(), customRaffle)

                newVoting
                    .let(customRaffleVotingModelMapper::mapReverse)
                    .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                    .onSuccess {
                        voting.postValue(newVoting)
                        readyToVote.postEvent(Unit)
                    }
            }
        }
    }

    fun resumeVoting(pin: String) {
        withVoting {
            when {
                pin.isBlank() -> pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_invalid))
                it.pin == pin.toInt() -> readyToVote.postEvent(Unit)
                else -> pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
            }
        }
    }

    fun vote(description: String) {
        withVoting { currentVoting ->
            startInBackground {
                val updatedVoting = currentVoting.copy(
                    totalVotes = currentVoting.totalVotes.inc(),
                    votes = currentVoting.votes.toMutableMap().apply {
                        set(description, currentVoting.votes.getOrDefaultValue(description, 0).inc())
                    }
                )

                updatedVoting.let(customRaffleVotingModelMapper::mapReverse)
                    .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                    .onSuccess {
                        voting.postValue(updatedVoting)
                        readyToVote.postEvent(Unit)
                    }
            }
        }
    }

    fun getVotingResults(pin: String) {
        withVoting { voting ->
            if (voting.pin == pin.toInt()) {
                startInBackground {
                    formatVotingResults(voting).onSuccess { list ->
                        customRaffleVotingDataSource.deleteCustomRaffleVoting(voting.customRaffleId)
                        results.postEvent(list)

                        if (voting.mostVoted.size > 1) showTieBreaker.postEvent(Unit)
                    }
                }
            } else {
                pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
            }
        }
    }

    fun setupTieBreakVoting() {
        withVoting { originalVoting ->
            startInBackground {
                val tieBreakVoting = originalVoting.copy(
                    totalVotes = 0,
                    votes = originalVoting.mostVoted.mapValues { 0 }
                )

                tieBreakVoting.let(customRaffleVotingModelMapper::mapReverse)
                    .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                    .onSuccess {
                        voting.postValue(tieBreakVoting)
                        readyToVote.postEvent(Unit)
                    }
            }
        }
    }

    fun setupRandomDecision(customRaffle: CustomRaffleModel) {
        withVoting { voting ->
            customRaffle.apply {
                items.forEach { it.included = it.description in voting.mostVoted.keys }
            }

            showRandomDecision.postEvent(customRaffle)
        }
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        voting.value?.let(body)
    }
}
