package com.fibelatti.raffler.features.lottery.presentation

import com.fibelatti.core.archcomponents.test.extension.currentValueShouldBe
import com.fibelatti.core.archcomponents.test.extension.shouldNeverReceiveValues
import com.fibelatti.core.extension.empty
import com.fibelatti.core.functional.Success
import com.fibelatti.core.test.extension.givenSuspend
import com.fibelatti.core.test.extension.mock
import com.fibelatti.core.test.extension.safeAny
import com.fibelatti.raffler.BaseViewModelTest
import com.fibelatti.raffler.MockDataProvider
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.preferences.Preferences
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given

class LotteryViewModelTest : BaseViewModelTest() {

    private val mockPreferencesRepository = mock<PreferencesRepository>()
    private val mockLotteryNumberModelMapper = mock<LotteryNumberModelMapper>()
    private val mockResourceProvider = mock<ResourceProvider>()

    private val mockPreferences = mock<Preferences>()
    private val mockLotteryNumberList = mock<List<LotteryNumberModel>>()

    private lateinit var viewModel: LotteryViewModel

    @BeforeEach
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
            mockResourceProvider
        )

        viewModel.defaultQuantityAvailable currentValueShouldBe MockDataProvider.genericString
        viewModel.defaultQuantityToRaffle currentValueShouldBe MockDataProvider.genericString
        viewModel.showHint.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityAvailableIsEmptyThenQuantityAvailableErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = " ", quantityToRaffle = "15")

        // THEN
        viewModel.quantityAvailableError currentValueShouldBe MockDataProvider.genericString
        viewModel.quantityToRaffleError.shouldNeverReceiveValues()
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityAvailableIsInvalidThenQuantityAvailableErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "abc", quantityToRaffle = "15")

        // THEN
        viewModel.quantityAvailableError currentValueShouldBe MockDataProvider.genericString
        viewModel.quantityToRaffleError.shouldNeverReceiveValues()
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityToRaffleIsEmptyThenQuantityToRaffleErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "10", quantityToRaffle = " ")

        // THEN
        viewModel.quantityAvailableError currentValueShouldBe String.empty()
        viewModel.quantityToRaffleError currentValueShouldBe MockDataProvider.genericString
        viewModel.lotteryNumbers.shouldNeverReceiveValues()
    }

    @Test
    fun whenGetLotteryNumbersIsCalledAndQuantityToRaffleIsInvalidThenQuantityToRaffleErrorReceivesError() {
        // WHEN
        viewModel.getLotteryNumbers(quantityAvailable = "10", quantityToRaffle = "abc")

        // THEN
        viewModel.quantityAvailableError currentValueShouldBe String.empty()
        viewModel.quantityToRaffleError currentValueShouldBe MockDataProvider.genericString
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
        viewModel.quantityAvailableError currentValueShouldBe String.empty()
        viewModel.quantityToRaffleError currentValueShouldBe String.empty()
        viewModel.error.shouldNeverReceiveValues()
        viewModel.lotteryNumbers currentValueShouldBe mockLotteryNumberList
    }
}
