package com.fibelatti.raffler.features.myraffles.presentation.voting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fibelatti.core.android.base.BaseViewType
import com.fibelatti.core.archcomponents.BaseViewModel
import com.fibelatti.core.archcomponents.LiveEvent
import com.fibelatti.core.archcomponents.MutableLiveEvent
import com.fibelatti.core.archcomponents.postEvent
import com.fibelatti.core.extension.getOrDefaultValue
import com.fibelatti.core.functional.onSuccess
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.FormatVotingResults
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleVotingDataSource
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_PIN_LENGTH = 4

class CustomRaffleVotingViewModel @Inject constructor(
    private val customRaffleVotingDataSource: CustomRaffleVotingDataSource,
    private val customRaffleVotingModelMapper: CustomRaffleVotingModelMapper,
    private val formatVotingResults: FormatVotingResults,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val ongoingVoting: LiveData<CustomRaffleModel> get() = _ongoingVoting
    private val _ongoingVoting = MutableLiveData<CustomRaffleModel>()
    val voting: LiveData<CustomRaffleVotingModel> get() = _voting
    private val _voting = MutableLiveData<CustomRaffleVotingModel>()
    val readyToVote: LiveEvent<Unit> get() = _readyToVote
    private val _readyToVote = MutableLiveEvent<Unit>()
    val showResults: LiveEvent<Unit> get() = _showResults
    private val _showResults = MutableLiveEvent<Unit>()
    val results: LiveData<List<BaseViewType>> get() = _results
    private val _results = MutableLiveData<List<BaseViewType>>()
    val pinError: LiveEvent<String> get() = _pinError
    private val _pinError = MutableLiveEvent<String>()
    val showTieBreaker: LiveEvent<Unit> get() = _showTieBreaker
    private val _showTieBreaker = MutableLiveEvent<Unit>()
    val showRandomDecision: LiveEvent<CustomRaffleModel> get() = _showRandomDecision
    private val _showRandomDecision = MutableLiveEvent<CustomRaffleModel>()

    fun checkForOngoingVoting(customRaffle: CustomRaffleModel) {
        launch {
            customRaffleVotingDataSource.getCustomRaffleVoting(customRaffle.id)
                .onSuccess { existingVoting ->
                    existingVoting?.let {
                        _ongoingVoting.postValue(customRaffle)
                        _voting.postValue(customRaffleVotingModelMapper.map(it))
                    }
                }
        }
    }

    fun setupNewVoting(pin: String, customRaffle: CustomRaffleModel) {
        if (pin.length < MIN_PIN_LENGTH) {
            _pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_invalid))
        } else {
            val newVoting = customRaffleVotingModelMapper.map(pin.toInt(), customRaffle)

            launch {
                customRaffleVotingModelMapper.mapReverse(newVoting)
                    .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                    .onSuccess {
                        _voting.postValue(newVoting)
                        _readyToVote.postEvent(Unit)
                    }
            }
        }
    }

    fun resumeVoting(pin: String) {
        val currentVoting = voting.value ?: return
        when {
            pin.isBlank() -> _pinError.postEvent(
                resourceProvider.getString(R.string.custom_raffle_voting_pin_invalid)
            )
            currentVoting.pin == pin.toInt() -> _readyToVote.postEvent(Unit)
            else -> _pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
        }
    }

    fun vote(description: String) {
        val currentVoting = voting.value ?: return

        launch {
            val updatedVoting = currentVoting.copy(
                totalVotes = currentVoting.totalVotes.inc(),
                votes = currentVoting.votes.toMutableMap().apply {
                    set(description, currentVoting.votes.getOrDefaultValue(description, 0).inc())
                }
            )

            updatedVoting.let(customRaffleVotingModelMapper::mapReverse)
                .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                .onSuccess {
                    _voting.postValue(updatedVoting)
                    _readyToVote.postEvent(Unit)
                }
        }
    }

    fun getVotingResults(pin: String) {
        val currentVoting = voting.value ?: return

        if (currentVoting.pin == pin.toInt()) {
            launch {
                formatVotingResults(currentVoting).onSuccess { list ->
                    customRaffleVotingDataSource.deleteCustomRaffleVoting(currentVoting.customRaffleId)
                    _showResults.postEvent(Unit)
                    _results.postValue(list)

                    if (currentVoting.mostVoted.size > 1) {
                        _showTieBreaker.postEvent(Unit)
                    }
                }
            }
        } else {
            _pinError.postEvent(resourceProvider.getString(R.string.custom_raffle_voting_pin_incorrect))
        }
    }

    fun setupTieBreakVoting() {
        val currentVoting = voting.value ?: return

        launch {
            val tieBreakVoting = currentVoting.copy(
                totalVotes = 0,
                votes = currentVoting.mostVoted.mapValues { 0 }
            )

            tieBreakVoting.let(customRaffleVotingModelMapper::mapReverse)
                .let { customRaffleVotingDataSource.saveCustomRaffleVoting(it) }
                .onSuccess {
                    _voting.postValue(tieBreakVoting)
                    _readyToVote.postEvent(Unit)
                }
        }
    }

    fun setupRandomDecision() {
        val currentRaffle = ongoingVoting.value ?: return
        val currentVoting = voting.value ?: return

        val newRaffle = currentRaffle.copy(
            items = currentRaffle.items.map {
                it.copy(included = it.description in currentVoting.mostVoted.keys)
            }
        )

        _showRandomDecision.postEvent(newRaffle)
    }
}
