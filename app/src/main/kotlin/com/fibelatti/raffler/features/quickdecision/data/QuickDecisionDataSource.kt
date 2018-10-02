package com.fibelatti.raffler.features.quickdecision.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.getOrThrow
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class QuickDecisionDataSource @Inject constructor(
    private val quickDecisionDao: QuickDecisionDao,
    private val resourceProvider: ResourceProvider,
    private val quickDecisionDtoMapper: QuickDecisionDtoMapper
) : QuickDecisionRepository {
    override suspend fun getAllQuickDecisions(): Result<List<QuickDecision>> {
        return runCatching {
            val dbList = quickDecisionDtoMapper.map(quickDecisionDao.getAllQuickDecisions())

            if (dbList.isNotEmpty()) {
                dbList
            } else {
                val localList = resourceProvider.getJsonFromAssets(
                    fileName = "quick-decisions.json",
                    type = object : TypeToken<List<QuickDecision>>() {})

                return@runCatching if (localList != null) {
                    addQuickDecisions(localList).getOrThrow()
                    localList
                } else {
                    throw RuntimeException("The file quick-decisions.json was not found.")
                }
            }
        }
    }

    override suspend fun addQuickDecisions(list: List<QuickDecision>): Result<Unit> =
        quickDecisionDao.runCatching { addQuickDecisions(quickDecisionDtoMapper.mapReverse(list)) }
}

@Dao
interface QuickDecisionDao {
    @Query("select * from $QUICK_DECISION_DTO_TABLE_NAME")
    fun getAllQuickDecisions(): List<QuickDecisionDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuickDecisions(list: List<QuickDecisionDto>)
}
