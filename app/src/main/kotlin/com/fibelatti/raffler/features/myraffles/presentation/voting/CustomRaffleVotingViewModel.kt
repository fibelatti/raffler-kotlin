package com.fibelatti.raffler.features.myraffles.presentation.voting

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.empty
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
private const val ONE_HUNDRED_PERCENT_MULTIPLIER = 100

class CustomRaffleVotingViewModel @Inject constructor(
    private val customRaffleVotingDataSource: CustomRaffleVotingDataSource,
    private val customRaffleVotingModelMapper: CustomRaffleVotingModelMapper,
    private val resourceProvider: ResourceProvider,
    coroutineLauncher: CoroutineLauncher
) : BaseViewModel(coroutineLauncher) {

    val ongoingVoting by lazy { MutableLiveEvent<Unit>() }
    val voting by lazy { MutableLiveData<CustomRaffleVotingModel>() }
    val readyToVote by lazy { MutableLiveEvent<Unit>() }
    val results by lazy { MutableLiveEvent<List<CustomRaffleVotingResultModel>>() }
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
                formatResults(voting)
            } else {
                pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
            }
        }
    }

    private fun formatResults(voting: CustomRaffleVotingModel) {
        startInBackground {
            val votingResult = voting.votes
                .toList().sortedByDescending { (_, value) -> value }
                .groupBy { (_, value) -> value }.toList()
                .mapIndexed { index, (_, tiedCandidates) ->
                    tiedCandidates.map { (description, numberOfVotes) ->
                        CustomRaffleVotingResultModel(
                            description = description,
                            numberOfVotes = numberOfVotes,
                            percentOfTotalVotes = (numberOfVotes / voting.totalVotes.toFloat()) * ONE_HUNDRED_PERCENT_MULTIPLIER,
                            additionalInfo = when (index) {
                                0 -> resourceProvider.getString(R.string.custom_raffle_voting_results_first_place)
                                1 -> resourceProvider.getString(R.string.custom_raffle_voting_results_second_place)
                                2 -> resourceProvider.getString(R.string.custom_raffle_voting_results_third_place)
                                else -> String.empty()
                            }
                        )
                    }
                }
                .flatten()

            customRaffleVotingDataSource.deleteCustomRaffleVoting(voting.customRaffleId)

            results.postEvent(votingResult)
        }
    }

    private fun withVoting(body: (CustomRaffleVotingModel) -> Unit) {
        voting.value?.let(body)
    }
}
