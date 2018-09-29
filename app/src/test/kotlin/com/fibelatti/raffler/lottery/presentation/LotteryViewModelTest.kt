package com.fibelatti.raffler.lottery.presentation

import androidx.lifecycle.Observer
import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.MockDataProvider
import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.extension.givenSuspend
import com.fibelatti.raffler.core.extension.safeAny
import com.fibelatti.raffler.core.functional.Either
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.lottery.presentation.LotteryNumberModel
import com.fibelatti.raffler.features.lottery.presentation.LotteryNumberModelMapper
import com.fibelatti.raffler.features.lottery.presentation.LotteryViewModel
import com.fibelatti.raffler.features.randomize.Randomize
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.never

class LotteryViewModelTest : BaseTest() {

    @Mock
    lateinit var mockRandomize: Randomize
    @Mock
    lateinit var mockLotteryNumberModelMapper: LotteryNumberModelMapper
    @Mock
    lateinit var mockResourceProvider: ResourceProvider

    @Mock
    lateinit var mockError: Throwable
    @Mock
    lateinit var mockRandomList: List<Int>
    @Mock
    lateinit var mockLotteryNumberList: List<LotteryNumberModel>

    @Mock
    lateinit var mockErrorObserver: Observer<Throwable>
    @Mock
    lateinit var mockLotteryNumbersObserver: Observer<List<LotteryNumberModel>>
    @Mock
    lateinit var mockTotalQuantityErrorObserver: Observer<String>
    @Mock
    lateinit var mockRaffleQuantityErrorObserver: Observer<String>

    private val viewModel by lazy {
        LotteryViewModel(
            mockRandomize,
            mockLotteryNumberModelMapper,
            mockResourceProvider,
            testThreadProvider
        )
    }

    private val inOrder by lazy {
        BDDMockito.inOrder(
            mockResourceProvider,
            mockErrorObserver,
            mockLotteryNumbersObserver,
            mockTotalQuantityErrorObserver,
            mockRaffleQuantityErrorObserver
        )
    }

    @Before
    fun setup() {
        given(mockResourceProvider.getString(anyInt()))
            .willReturn(MockDataProvider.genericString)

        viewModel.run {
            error.observeForever(mockErrorObserver)
            lotteryNumbers.observeForever(mockLotteryNumbersObserver)
            totalQuantityError.observeForever(mockTotalQuantityErrorObserver)
            raffleQuantityError.observeForever(mockRaffleQuantityErrorObserver)
        }
    }

    @Test
    fun `WHEN getLotteryNumbers is called AND totalQuantity is empty THEN totalQuantityError receives an error`() {
        // WHEN
        viewModel.getLotteryNumbers(totalQuantity = " ", raffleQuantity = "15")

        // THEN
        inOrder.run {
            verify(mockTotalQuantityErrorObserver).onChanged(MockDataProvider.genericString)
            verify(mockRaffleQuantityErrorObserver, never()).onChanged(anyString())
            verify(mockLotteryNumbersObserver, never()).onChanged(any())
        }
    }

    @Test
    fun `WHEN getLotteryNumbers is called AND totalQuantity is invalid THEN totalQuantityError is changed`() {
        // WHEN
        viewModel.getLotteryNumbers(totalQuantity = "abc", raffleQuantity = "15")

        // THEN
        inOrder.run {
            verify(mockTotalQuantityErrorObserver).onChanged(MockDataProvider.genericString)
            verify(mockRaffleQuantityErrorObserver, never()).onChanged(anyString())
            verify(mockLotteryNumbersObserver, never()).onChanged(any())
        }
    }

    @Test
    fun `WHEN getLotteryNumbers is called AND raffleQuantity is empty THEN raffleQuantityError is changed`() {
        // WHEN
        viewModel.getLotteryNumbers(totalQuantity = "10", raffleQuantity = " ")

        // THEN
        inOrder.run {
            verify(mockTotalQuantityErrorObserver).onChanged(String.empty())
            verify(mockRaffleQuantityErrorObserver).onChanged(MockDataProvider.genericString)
            verify(mockLotteryNumbersObserver, never()).onChanged(any())
        }
    }

    @Test
    fun `WHEN getLotteryNumbers is called AND raffleQuantity is invalid THEN raffleQuantityError is changed`() {
        // WHEN
        viewModel.getLotteryNumbers(totalQuantity = "10", raffleQuantity = "abc")

        // THEN
        inOrder.run {
            verify(mockTotalQuantityErrorObserver).onChanged(String.empty())
            verify(mockRaffleQuantityErrorObserver).onChanged(MockDataProvider.genericString)
            verify(mockLotteryNumbersObserver, never()).onChanged(any())
        }
    }

    @Test
    fun `WHEN getLotteryNumbers is called AND randomize throws an error THEN error is changed`() {
        // GIVEN
        givenSuspend { mockRandomize(safeAny()) }
            .willReturn(Either.left(mockError))

        // WHEN
        viewModel.getLotteryNumbers(totalQuantity = "10", raffleQuantity = "5")

        // THEN
        inOrder.run {
            verify(mockTotalQuantityErrorObserver).onChanged(String.empty())
            verify(mockRaffleQuantityErrorObserver).onChanged(String.empty())
            verify(mockErrorObserver).onChanged(mockError)
            verify(mockLotteryNumbersObserver, never()).onChanged(any())
        }
    }

    @Test
    fun `WHEN getLotteryNumbers is called AND lotteryNumbers is changed`() {
        // GIVEN
        givenSuspend { mockRandomize(safeAny()) }
            .willReturn(Either.right(mockRandomList))
        given(mockLotteryNumberModelMapper.map(mockRandomList))
            .willReturn(mockLotteryNumberList)

        // WHEN
        viewModel.getLotteryNumbers(totalQuantity = "10", raffleQuantity = "5")

        // THEN
        inOrder.run {
            verify(mockTotalQuantityErrorObserver).onChanged(String.empty())
            verify(mockRaffleQuantityErrorObserver).onChanged(String.empty())
            verify(mockErrorObserver, never()).onChanged(any())
            verify(mockLotteryNumbersObserver).onChanged(mockLotteryNumberList)
        }
    }
}
