package com.fibelatti.raffler.features.lottery.presentation

import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.MockDataProvider
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.givenSuspend
import com.fibelatti.raffler.core.extension.mock
import com.fibelatti.raffler.core.extension.safeAny
import com.fibelatti.raffler.core.extension.shouldNeverReceiveValues
import com.fibelatti.raffler.core.extension.shouldReceiveOnly
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.preferences.Preferences
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given

class LotteryViewModelTest : BaseTest() {

    private val mockPreferencesRepository = mock<PreferencesRepository>()
    private val mockLotteryNumberModelMapper = mock<LotteryNumberModelMapper>()
    private val mockResourceProvider = mock<ResourceProvider>()

    private val mockPreferences = mock<Preferences>()
    private val mockLotteryNumberList = mock<List<LotteryNumberModel>>()

    private lateinit var viewModel: LotteryViewModel

    @Before
    fun setup() {
        given(mockResourceProvider.getString(anyInt()))
            .willReturn(MockDataProvider.genericString)
        givenSuspend { mockPreferencesRepository.getPreferences() }
            .willReturn(Success(mockPreferences))
        given(mockPreferences.lotteryDefaultQuantityAvailable)
            .willReturn(MockDataProvider.genericString)
        given(mockPreferences.lotteryDefaultQuantityToRaffle)
            .willReturn(MockDataProvider.genericString)
        givenSuspend { mockPreferencesRepository.getLotteryHintDisplayed() }
            .willReturn(true)

        viewModel = LotteryViewModel(
            mockPreferencesRepository,
            mockLotteryNumberModelMapper,
            mockResourceProvider,
            testCoroutineLauncher
        )

        viewModel.defaultQuantityAvailable shouldReceiveOnly MockDataProvider.genericString
        viewModel.defaultQuantityToRaffle shouldReceiveOnly MockDataProvider.genericString
        viewModel.showHint.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityAvailableIsEmptyThenQuantityAvailableErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = " ", quantityToRaffle = "15")

        // THEN
        viewModel.quantityAvailableError shouldReceiveOnly MockDataProvider.genericString
        viewModel.quantityToRaffleError.shouldNeverReceiveValues()
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityAvailableIsInvalidThenQuantityAvailableErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "abc", quantityToRaffle = "15")

        // THEN
        viewModel.quantityAvailableError shouldReceiveOnly MockDataProvider.genericString
        viewModel.quantityToRaffleError.shouldNeverReceiveValues()
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityToRaffleIsEmptyThenQuantityToRaffleErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "10", quantityToRaffle = " ")

        // THEN
        viewModel.quantityAvailableError shouldReceiveOnly String.empty()
        viewModel.quantityToRaffleError shouldReceiveOnly MockDataProvider.genericString
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityToRaffleIsInvalidThenQuantityToRaffleErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "10", quantityToRaffle = "abc")

        // THEN
        viewModel.quantityAvailableError shouldReceiveOnly String.empty()
        viewModel.quantityToRaffleError shouldReceiveOnly MockDataProvider.genericString
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledThenLotteryNumbersReceivesValue() {
        // GIVEN
        given(mockLotteryNumberModelMapper.mapList(safeAny()))
            .willReturn(mockLotteryNumberList)

        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "10", quantityToRaffle = "5")

        // THEN
        viewModel.quantityAvailableError shouldReceiveOnly String.empty()
        viewModel.quantityToRaffleError shouldReceiveOnly String.empty()
        viewModel.error.shouldNeverReceiveValues()
        viewModel.lotteryNumbers shouldReceiveOnly mockLotteryNumberList
    }
}
