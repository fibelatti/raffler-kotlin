package com.fibelatti.raffler.features.myraffles.presentation.customraffledetails

import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.MockDataProvider
import com.fibelatti.raffler.MockDataProvider.mockCustomRaffle
import com.fibelatti.raffler.MockDataProvider.mockCustomRaffleItem
import com.fibelatti.raffler.MockDataProvider.mockCustomRaffleItemModel
import com.fibelatti.raffler.MockDataProvider.mockCustomRaffleModel
import com.fibelatti.raffler.MockDataProvider.mockPreferences
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.asLiveData
import com.fibelatti.raffler.core.extension.givenSuspend
import com.fibelatti.raffler.core.extension.mock
import com.fibelatti.raffler.core.extension.safeAny
import com.fibelatti.raffler.core.extension.shouldReceive
import com.fibelatti.raffler.core.extension.shouldReceiveEventWithValue
import com.fibelatti.raffler.core.extension.verifySuspend
import com.fibelatti.raffler.core.functional.Failure
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.RememberRaffled
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CustomRaffleDetailsViewModelTest : BaseTest() {

    private val mockPreferencesRepository = mock<PreferencesRepository>()
    private val mockCustomRaffleRepository = mock<CustomRaffleRepository>()
    private val mockCustomRaffleModelMapper = mock<CustomRaffleModelMapper>()
    private val mockRememberRaffled = mock<RememberRaffled>()
    private val mockResourceProvider = mock<ResourceProvider>()

    private val mockException = Exception()
    private val mockFailure = Failure(mockException)

    private lateinit var viewModel: CustomRaffleDetailsViewModel

    @Before
    fun setup() {
        given(mockResourceProvider.getString(anyInt()))
            .willReturn(MockDataProvider.genericString)
        givenSuspend { mockPreferencesRepository.getRaffleDetailsHintDisplayed() }
            .willReturn(false)

        viewModel = spy(CustomRaffleDetailsViewModel(
            mockPreferencesRepository,
            mockCustomRaffleRepository,
            mockCustomRaffleModelMapper,
            mockRememberRaffled,
            mockResourceProvider,
            testCoroutineLauncher
        ))

        viewModel.showHint shouldReceiveEventWithValue Unit
    }

    // region getCustomRaffleById
    @Test
    fun `GIVEN getCustomRaffleById is called WHEN getPreferences fail THEN default values are posted`() {
        // GIVEN
        givenSuspend { mockPreferencesRepository.getPreferences() }
            .willReturn(mockFailure)
        givenSuspend { mockCustomRaffleRepository.getCustomRaffleById(id = 0) }
            .willReturn(mockFailure)

        // WHEN
        viewModel.getCustomRaffleById(id = 0)

        // THEN
        with(viewModel) {
            preferredRaffleMode shouldReceive AppConfig.RaffleMode.NONE
            rouletteMusicEnabled shouldReceive false
            rememberRaffledItems shouldReceive false
            error shouldReceive mockException
        }
    }

    @Test
    fun `GIVEN getCustomRaffleById is called WHEN getPreferences returns THEN values are posted`() {
        // GIVEN
        arrangePreferences()
        givenSuspend { mockCustomRaffleRepository.getCustomRaffleById(id = 0) }
            .willReturn(mockFailure)

        // WHEN
        viewModel.getCustomRaffleById(id = 0)

        // THEN
        assertPreferences()
        viewModel.error shouldReceive mockException
    }

    @Test
    fun `GIVEN getCustomRaffleById is called WHEN rememberRaffledItems is false THEN customRaffle receives value AND all its items are included AND toggleState receives EXCLUDE_ALL`() {
        testGetCustomRaffleByIdHappyPath(
            mockCustomRaffleModel(
                items = mutableListOf(
                    mockCustomRaffleItemModel(included = true),
                    mockCustomRaffleItemModel(included = false)
                )
            ),
            expectedRaffleModel = mockCustomRaffleModel(
                items = mutableListOf(
                    mockCustomRaffleItemModel(included = true),
                    mockCustomRaffleItemModel(included = true)
                )
            ),
            rememberRaffled = false,
            expectedToggleState = CustomRaffleDetailsViewModel.ToggleState.EXCLUDE_ALL
        )
    }

    @Test
    fun `GIVEN getCustomRaffleById is called WHEN any of its items have included equals false THEN customRaffle receives value AND all its items are included AND toggleState receives INCLUDE_ALL`() {
        testGetCustomRaffleByIdHappyPath(
            mockCustomRaffleModel(
                items = mutableListOf(
                    mockCustomRaffleItemModel(included = true),
                    mockCustomRaffleItemModel(included = false)
                )
            ),
            expectedToggleState = CustomRaffleDetailsViewModel.ToggleState.INCLUDE_ALL
        )
    }

    @Test
    fun `GIVEN getCustomRaffleById is called WHEN all of its items have included equals false THEN customRaffle receives value AND all its items are included AND toggleState receives INCLUDE_ALL`() {
        testGetCustomRaffleByIdHappyPath(
            mockCustomRaffleModel(
                items = mutableListOf(
                    mockCustomRaffleItemModel(included = false),
                    mockCustomRaffleItemModel(included = false)
                )
            ),
            expectedToggleState = CustomRaffleDetailsViewModel.ToggleState.INCLUDE_ALL
        )
    }

    private fun testGetCustomRaffleByIdHappyPath(
        mappedRaffleModel: CustomRaffleModel,
        expectedRaffleModel: CustomRaffleModel = mappedRaffleModel,
        rememberRaffled: Boolean = true,
        expectedToggleState: CustomRaffleDetailsViewModel.ToggleState
    ) {
        // GIVEN
        val customRaffle = mockCustomRaffle(
            items = listOf(
                mockCustomRaffleItem(included = true),
                mockCustomRaffleItem(included = false)
            )
        )

        arrangePreferences(rememberRaffled)
        givenSuspend { mockCustomRaffleRepository.getCustomRaffleById(id = MockDataProvider.defaultId) }
            .willReturn(Success(customRaffle))
        given(mockCustomRaffleModelMapper.map(customRaffle))
            .willReturn(mappedRaffleModel)

        // WHEN
        viewModel.getCustomRaffleById(id = MockDataProvider.defaultId)

        // THEN
        assertPreferences(rememberRaffled)
        viewModel.customRaffle shouldReceive expectedRaffleModel
        viewModel.toggleState shouldReceiveEventWithValue expectedToggleState
    }

    private fun arrangePreferences(rememberRaffled: Boolean = true) {
        givenSuspend { mockPreferencesRepository.getPreferences() }
            .willReturn(
                Success(mockPreferences(
                    preferredRaffleMode = AppConfig.RaffleMode.ROULETTE,
                    rouletteMusicEnabled = true,
                    rememberRaffledItems = rememberRaffled
                ))
            )
    }

    private fun assertPreferences(rememberRaffled: Boolean = true) {
        with(viewModel) {
            preferredRaffleMode shouldReceive AppConfig.RaffleMode.ROULETTE
            rouletteMusicEnabled shouldReceive true
            rememberRaffledItems shouldReceive rememberRaffled
        }
    }
    // endregion

    // region updateItemSelection
    @Test
    fun `GIVEN index is not valid for current CustomRaffle WHEN updateItemSelection is called THEN nothing happens`() {
        // GIVEN
        val initialValue = mockCustomRaffleModel(
            items = mutableListOf(mockCustomRaffleItemModel(included = false))
        )

        given(viewModel.customRaffle)
            .willReturn(initialValue.asLiveData())

        // WHEN
        viewModel.updateItemSelection(index = 1, isSelected = true)

        // THEN
        verifySuspend(mockRememberRaffled, never()) { invoke(safeAny()) }
        viewModel.customRaffle shouldReceive initialValue
    }

    @Test
    fun `GIVEN index is valid for current CustomRaffle WHEN updateItemSelection is called THEN rememberRaffle is called AND customRaffle receives the updated value`() {
        // GIVEN
        val initialItem = mockCustomRaffleItemModel(included = false)
        val initialValue = mockCustomRaffleModel(items = mutableListOf(initialItem))
        val expectedValue = mockCustomRaffleModel(
            items = mutableListOf(mockCustomRaffleItemModel(included = true))
        )

        given(viewModel.customRaffle)
            .willReturn(initialValue.asLiveData())
            .willCallRealMethod()

        // WHEN
        viewModel.updateItemSelection(index = 0, isSelected = true)

        // THEN
        verifySuspend(mockRememberRaffled) { invoke(RememberRaffled.Params(initialItem, true)) }
        viewModel.customRaffle shouldReceive expectedValue
    }
    // endregion

    // region raffle
    @Test
    fun `GIVEN the item selection is invalid WHEN raffle is called THEN  invalidSelectionError receives an error message`() {
        // GIVEN
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(
                    items = mutableListOf(
                        mockCustomRaffleItemModel(included = false),
                        mockCustomRaffleItemModel(included = false)
                    )
                ).asLiveData()
            )

        // WHEN
        viewModel.raffle()

        // THEN
        verify(mockResourceProvider).getString(R.string.custom_raffle_details_mode_invalid_quantity)
        viewModel.invalidSelectionError shouldReceiveEventWithValue MockDataProvider.genericString
    }

    @Test
    fun `GIVEN the item selection is valid WHEN raffle is called THEN showPreferredRaffleMode receives a value`() {
        // GIVEN
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(
                    items = mutableListOf(
                        mockCustomRaffleItemModel(included = true),
                        mockCustomRaffleItemModel(included = true)
                    )
                ).asLiveData()
            )
        given(viewModel.preferredRaffleMode)
            .willReturn(AppConfig.RaffleMode.ROULETTE.asLiveData())

        // WHEN
        viewModel.raffle()

        // THEN
        viewModel.showPreferredRaffleMode shouldReceiveEventWithValue AppConfig.RaffleMode.ROULETTE
    }
    // endregion

    // region selectMode
    @Test
    fun `GIVEN the item selection is invalid WHEN selectMode is called THEN  invalidSelectionError receives an error message`() {
        // GIVEN
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(
                    items = mutableListOf(
                        mockCustomRaffleItemModel(included = false),
                        mockCustomRaffleItemModel(included = false)
                    )
                ).asLiveData()
            )

        // WHEN
        viewModel.selectMode()

        // THEN
        verify(mockResourceProvider).getString(R.string.custom_raffle_details_mode_invalid_quantity)
        viewModel.invalidSelectionError shouldReceiveEventWithValue MockDataProvider.genericString
    }

    @Test
    fun `GIVEN the item selection is valid WHEN selectMode is called THEN showModeSelector receives a value`() {
        // GIVEN
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(
                    items = mutableListOf(
                        mockCustomRaffleItemModel(included = true),
                        mockCustomRaffleItemModel(included = true)
                    )
                ).asLiveData()
            )

        // WHEN
        viewModel.selectMode()

        // THEN
        viewModel.showModeSelector shouldReceiveEventWithValue Unit
    }
    // endregion

    // region itemRaffled
    @Test
    fun `GIVEN rememberRaffledItems is false WHEN itemRaffled is called THEN nothing happens`() {
        // GIVEN
        given(viewModel.rememberRaffledItems)
            .willReturn(false.asLiveData())
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(
                    items = mutableListOf(
                        mockCustomRaffleItemModel(included = false),
                        mockCustomRaffleItemModel(included = false)
                    )
                ).asLiveData()
            )

        // WHEN
        viewModel.itemRaffled(0)

        // THEN
        verifySuspend(mockRememberRaffled, never()) { invoke(safeAny()) }
        verifySuspend(mockCustomRaffleRepository, never()) { getCustomRaffleById(anyLong()) }
    }

    @Test
    fun `GIVEN rememberRaffledItems is true AND the index is invalid WHEN itemRaffled is called THEN nothing happens`() {
        // GIVEN
        given(viewModel.rememberRaffledItems)
            .willReturn(true.asLiveData())
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(
                    items = mutableListOf(mockCustomRaffleItemModel(included = false))
                ).asLiveData()
            )

        // WHEN
        viewModel.itemRaffled(1)

        // THEN
        verifySuspend(mockRememberRaffled, never()) { invoke(safeAny()) }
        verifySuspend(mockCustomRaffleRepository, never()) { getCustomRaffleById(anyLong()) }
    }

    @Test
    fun `GIVEN rememberRaffledItems is true AND rememberRaffled fails WHEN itemRaffled is called THEN no new values are posted`() {
        // GIVEN
        val firstItem = mockCustomRaffleItemModel(id = 1, included = false)
        val secondItem = mockCustomRaffleItemModel(id = 2, included = false)

        given(viewModel.rememberRaffledItems)
            .willReturn(true.asLiveData())
        given(viewModel.customRaffle)
            .willReturn(
                mockCustomRaffleModel(items = mutableListOf(firstItem, secondItem))
                    .asLiveData()
            )
        givenSuspend { mockRememberRaffled(RememberRaffled.Params(firstItem, included = false)) }
            .willReturn(Success(Unit))
        givenSuspend { mockRememberRaffled(RememberRaffled.Params(secondItem, included = false)) }
            .willReturn(mockFailure)

        // WHEN
        viewModel.itemRaffled(0, 1)

        // THEN
        verifySuspend(mockRememberRaffled, times(2)) { invoke(safeAny()) }
        verifySuspend(mockCustomRaffleRepository, never()) { getCustomRaffleById(anyLong()) }
    }

    @Test
    fun `GIVEN rememberRaffledItems is true AND rememberRaffled succeeds WHEN itemRaffled is called THEN updated values are posted`() {
        // GIVEN
        val startingCustomRaffleModel = mockCustomRaffleModel(
            items = mutableListOf(mockCustomRaffleItemModel(), mockCustomRaffleItemModel())
        )
        val customRaffle = mockCustomRaffle(
            items = listOf(mockCustomRaffleItem(included = false), mockCustomRaffleItem(included = false))
        )
        val expectedCustomRaffleModel = mockCustomRaffleModel(
            id = 2,
            items = mutableListOf(mockCustomRaffleItemModel(), mockCustomRaffleItemModel())
        )

        given(viewModel.rememberRaffledItems)
            .willReturn(true.asLiveData())
        given(viewModel.customRaffle)
            .willReturn(startingCustomRaffleModel.asLiveData())
            .willCallRealMethod()
        givenSuspend { mockRememberRaffled(safeAny()) }
            .willReturn(Success(Unit))
        givenSuspend { mockCustomRaffleRepository.getCustomRaffleById(startingCustomRaffleModel.id) }
            .willReturn(Success(customRaffle))
        given(mockCustomRaffleModelMapper.map(customRaffle))
            .willReturn(expectedCustomRaffleModel)

        // WHEN
        viewModel.itemRaffled(0, 1)

        // THEN
        verifySuspend(mockRememberRaffled, times(2)) { invoke(safeAny()) }
        verifySuspend(mockCustomRaffleRepository) { getCustomRaffleById(startingCustomRaffleModel.id) }
        viewModel.customRaffle shouldReceive expectedCustomRaffleModel
        viewModel.itemsRemaining shouldReceiveEventWithValue expectedCustomRaffleModel.includedItems.size
    }
    // endregion

    // region hintDismissed
    @Test
    fun `WHEN hintDismissed is called THEN preferencesRepository setRaffleDetailsHintDismissed is called`() {
        // WHEN
        viewModel.hintDismissed()

        // THEN
        verifySuspend(mockPreferencesRepository) { setRaffleDetailsHintDismissed() }
    }
    // endregion

    // region setCustomRaffleForContinuation
    @Test
    fun `WHEN setCustomRaffleForContinuation is called THEN customRaffle value is updated`() {
        // WHEN
        viewModel.setCustomRaffleForContinuation(mockCustomRaffleModel())

        // THEN
        viewModel.customRaffle shouldReceive mockCustomRaffleModel()
    }
    // endregion

    // region toggleAll
    @Test
    fun `GIVEN any rememberRaffled fails WHEN toggleAll is called THEN nothing else happens`() {
    }

    @Test
    fun `GIVEN all rememberRaffled succeeds AND toggleState is INCLUDE_ALL WHEN toggleAll is called THEN customRaffle receives a value with all items included AND toggleState receives EXCLUDE_ALL`() {
    }

    @Test
    fun `GIVEN all rememberRaffled succeeds AND toggleState is EXCLUDE_ALL WHEN toggleAll is called THEN customRaffle receives a value with all items not included AND toggleState receives INCLUDE_ALL`() {
    }
    // endregion
}
