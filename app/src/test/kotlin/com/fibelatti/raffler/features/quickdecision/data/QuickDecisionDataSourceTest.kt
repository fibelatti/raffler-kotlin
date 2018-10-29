package com.fibelatti.raffler.features.quickdecision.data

import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.MockDataProvider
import com.fibelatti.raffler.core.extension.callSuspend
import com.fibelatti.raffler.core.extension.givenSuspend
import com.fibelatti.raffler.core.extension.mock
import com.fibelatti.raffler.core.extension.shouldBe
import com.fibelatti.raffler.core.extension.shouldBeAnInstanceOf
import com.fibelatti.raffler.core.functional.Failure
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.functional.exceptionOrNull
import com.fibelatti.raffler.core.functional.getOrNull
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given

class QuickDecisionDataSourceTest : BaseTest() {

    private val mockQuickDecisionDao = mock<QuickDecisionDao>()
    private val mockResourceProvider = mock<ResourceProvider>()
    private val mockQuickDecisionDtoMapper = mock<QuickDecisionDtoMapper>()

    private val mockQuickDecisionDto = mock<QuickDecisionDto>()
    private val mockQuickDecision = mock<QuickDecision>()
    private val mockQuickDecisionDtoList = listOf(mockQuickDecisionDto)
    private val mockQuickDecisionList = listOf(mockQuickDecision)

    private val mockError = mock<Throwable>()

    private val quickDecisionDataSource by lazy {
        QuickDecisionDataSource(
            mockQuickDecisionDao,
            mockResourceProvider,
            mockQuickDecisionDtoMapper
        )
    }

    @BeforeEach
    fun setup() {
        given(mockResourceProvider.getString(anyInt()))
            .willReturn(MockDataProvider.genericString)
        given(mockQuickDecisionDtoMapper.map(mockQuickDecisionDto))
            .willReturn(mockQuickDecision)
        given(mockQuickDecisionDtoMapper.mapList(mockQuickDecisionDtoList))
            .willReturn(mockQuickDecisionList)
        given(mockQuickDecisionDtoMapper.mapReverse(mockQuickDecision))
            .willReturn(mockQuickDecisionDto)
        given(mockQuickDecisionDtoMapper.mapListReverse(mockQuickDecisionList))
            .willReturn(mockQuickDecisionDtoList)
    }

    @Test
    fun `WHEN quickDecisionDao getAllQuickDecisions returns items THEN Success is returned`() {
        // GIVEN
        givenSuspend { mockQuickDecisionDao.getAllQuickDecisions() }
            .willReturn(mockQuickDecisionDtoList)

        // WHEN
        val result = callSuspend { quickDecisionDataSource.getAllQuickDecisions() }

        // THEN
        result.shouldBeAnInstanceOf<Success<*>>()
        result.getOrNull() shouldBe mockQuickDecisionList
    }

    @Test
    fun `WHEN quickDecisionDao getAllQuickDecisions returns empty AND getJsonFromAssets is returns null THEN Failure is returned`() {
        // GIVEN
        val typeToken = object : TypeToken<List<QuickDecisionDto>>() {}

        givenSuspend { mockQuickDecisionDao.getAllQuickDecisions() }
            .willReturn(emptyList())
        given(mockResourceProvider.getJsonFromAssets("quick-decisions.json", typeToken))
            .willReturn(null)

        // WHEN
        val result = callSuspend { quickDecisionDataSource.getAllQuickDecisions() }

        // THEN
        result.shouldBeAnInstanceOf<Failure>()
        result.exceptionOrNull()?.shouldBeAnInstanceOf<RuntimeException>()
    }

    @Test
    fun `WHEN quickDecisionDao getAllQuickDecisions returns empty AND getJsonFromAssets is returns a list AND addQuickDecisions fails THEN Failure is returned`() {
        // GIVEN
        val typeToken = object : TypeToken<List<QuickDecisionDto>>() {}

        givenSuspend { mockQuickDecisionDao.getAllQuickDecisions() }
            .willReturn(emptyList())
        given(mockResourceProvider.getJsonFromAssets("quick-decisions.json", typeToken))
            .willReturn(mockQuickDecisionDtoList)

        givenSuspend { mockQuickDecisionDao.addQuickDecisions(mockQuickDecisionDtoList) }
            .willAnswer { throw mockError }

        // WHEN
        val result = callSuspend { quickDecisionDataSource.getAllQuickDecisions() }

        // THEN
        result.shouldBeAnInstanceOf<Failure>()
        result.exceptionOrNull() shouldBe mockError
    }

    @Test
    fun `WHEN quickDecisionDao getAllQuickDecisions returns empty AND getJsonFromAssets is returns a list AND addQuickDecisions succeeds THEN Success is returned`() {
        // GIVEN
        val typeToken = object : TypeToken<List<QuickDecisionDto>>() {}

        givenSuspend { mockQuickDecisionDao.getAllQuickDecisions() }
            .willReturn(emptyList())
        given(mockResourceProvider.getJsonFromAssets("quick-decisions.json", typeToken))
            .willReturn(mockQuickDecisionDtoList)

        // WHEN
        val result = callSuspend { quickDecisionDataSource.getAllQuickDecisions() }

        // THEN
        result.shouldBeAnInstanceOf<Success<*>>()
        result.getOrNull() shouldBe mockQuickDecisionList
    }

    @Test
    fun `WHEN addQuickDecisions is called AND quickDecisionDao throws an error THEN Failure is returned`() {
        // GIVEN
        givenSuspend { mockQuickDecisionDao.addQuickDecisions(mockQuickDecisionDtoList) }
            .willAnswer { throw mockError }

        // WHEN
        val result = callSuspend { quickDecisionDataSource.addQuickDecisions(mockQuickDecision) }

        // THEN
        result.shouldBeAnInstanceOf<Failure>()
        result.exceptionOrNull() shouldBe mockError
    }

    @Test
    fun `WHEN addQuickDecisions is called THEN Success is returned`() {
        // WHEN
        val result = callSuspend { quickDecisionDataSource.addQuickDecisions(mockQuickDecision) }

        // THEN
        result.shouldBeAnInstanceOf<Success<*>>()
    }
}
