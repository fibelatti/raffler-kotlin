package com.fibelatti.raffler.features.quickdecision.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.fibelatti.raffler.core.functional.Either
import com.fibelatti.raffler.core.functional.flatMap
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.features.quickdecision.QuickDecision
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class QuickDecisionDataSource @Inject constructor(
    private val localSource: QuickDecisionDao,
    private val resourceProvider: ResourceProvider,
    private val quickDecisionDtoMapper: QuickDecisionDtoMapper
) : QuickDecisionRepository {
    override suspend fun getAllQuickDecisions(): Either<Throwable, List<QuickDecision>> {
        return try {
            val dbList = quickDecisionDtoMapper.map(localSource.getAllQuickDecisions())

            if (dbList.isNotEmpty()) {
                Either.right(dbList)
            } else {
                val localList = resourceProvider.getJsonFromAssets(
                    fileName = "quick-decisions.json",
                    type = object : TypeToken<List<QuickDecision>>() {})

                if (localList != null) {
                    addQuickDecisions(localList).flatMap { Either.right(localList) }
                } else {
                    Either.left(RuntimeException("The file quick-decisions.json was not found."))
                }
            }
        } catch (exception: Exception) {
            Either.left(exception)
        }
    }

    override suspend fun addQuickDecisions(list: List<QuickDecision>): Either<Throwable, Unit> =
        localSource.runCatching { addQuickDecisions(quickDecisionDtoMapper.mapReverse(list)) }
}

@Dao
interface QuickDecisionDao {
    @Query("select * from $QUICK_DECISION_DTO_TABLE_NAME")
    fun getAllQuickDecisions(): List<QuickDecisionDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuickDecisions(list: List<QuickDecisionDto>)
}
