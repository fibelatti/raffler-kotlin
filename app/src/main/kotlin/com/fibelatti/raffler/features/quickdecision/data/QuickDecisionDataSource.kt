package com.fibelatti.raffler.features.quickdecision.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fibelatti.core.functional.Result
import com.fibelatti.raffler.core.functional.resultFrom
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuickDecisionDataSource @Inject constructor(
    private val quickDecisionDao: QuickDecisionDao,
    private val resourceProvider: ResourceProvider,
    private val quickDecisionDtoMapper: QuickDecisionDtoMapper
) : QuickDecisionRepository {

    override suspend fun getAllQuickDecisions(): Result<List<QuickDecision>> {
        return resultFrom {
            val dbList = withContext(Dispatchers.IO) {
                quickDecisionDao.getAllQuickDecisions()
            }.let(quickDecisionDtoMapper::mapList)

            if (dbList.isNotEmpty()) {
                dbList
            } else {
                val localList = resourceProvider.getJsonFromAssets(
                    fileName = "quick-decisions.json",
                    type = object : TypeToken<List<QuickDecisionDto>>() {}
                )

                return@resultFrom if (localList != null) {
                    withContext(Dispatchers.IO) {
                        quickDecisionDao.addQuickDecisions(localList)
                    }
                    quickDecisionDtoMapper.mapList(localList)
                } else {
                    throw RuntimeException("The file quick-decisions.json was not found.")
                }
            }
        }
    }

    override suspend fun getQuickDecisionById(id: String): Result<QuickDecision?> = resultFrom {
        withContext(Dispatchers.IO) {
            quickDecisionDao.getQuickDecisionById(id)
        }?.let(quickDecisionDtoMapper::map)
    }

    override suspend fun deleteQuickDecisionById(id: String): Result<Unit> = resultFrom {
        withContext(Dispatchers.IO) {
            quickDecisionDao.deleteQuickDecisionById(id)
        }
    }

    override suspend fun addQuickDecisions(vararg quickDecision: QuickDecision): Result<Unit> = resultFrom {
        withContext(Dispatchers.IO) {
            quickDecision.map(quickDecisionDtoMapper::mapReverse)
                .let(quickDecisionDao::addQuickDecisions)
        }
    }
}

@Dao
interface QuickDecisionDao {

    @Query("select * from $QUICK_DECISION_DTO_TABLE_NAME")
    fun getAllQuickDecisions(): List<QuickDecisionDto>

    @Query("select * from $QUICK_DECISION_DTO_TABLE_NAME where $QUICK_DECISION_DTO_COLUMN_ID = :id")
    fun getQuickDecisionById(id: String): QuickDecisionDto?

    @Query("delete from $QUICK_DECISION_DTO_TABLE_NAME where $QUICK_DECISION_DTO_COLUMN_ID = :id")
    fun deleteQuickDecisionById(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuickDecisions(list: List<QuickDecisionDto>)
}
