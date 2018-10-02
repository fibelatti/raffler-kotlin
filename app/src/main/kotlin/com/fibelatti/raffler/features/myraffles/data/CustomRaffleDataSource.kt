package com.fibelatti.raffler.features.myraffles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.core.persistence.database.AppDatabase
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import javax.inject.Inject

class CustomRaffleDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val customRaffleDao: CustomRaffleDao,
    private val customRaffleItemDao: CustomRaffleItemDao,
    private val customRaffleWithItemsDtoMapper: CustomRaffleWithItemsDtoMapper
) : CustomRaffleRepository {
    override suspend fun getAllCustomRaffles(): Result<List<CustomRaffle>> {
        return runCatching {
            customRaffleDao.getAllCustomRaffles()
                .map(customRaffleWithItemsDtoMapper::map)
        }
    }

    override suspend fun getCustomRaffleById(id: Long): Result<CustomRaffle> {
        return runCatching {
            customRaffleDao.getCustomRaffleById(id)
                .let(customRaffleWithItemsDtoMapper::map)
        }
    }

    override suspend fun addCustomRaffle(customRaffle: CustomRaffle): Result<Unit> {
        return runCatching {
            with(customRaffle.let(customRaffleWithItemsDtoMapper::mapReverse)) {
                appDatabase.runInTransaction {
                    customRaffleDao.deleteCustomRaffleById(customRaffleDto.id)
                    customRaffleDao.saveCustomRaffle(customRaffleDto)
                    customRaffleItemDao.saveCustomRaffleItems(*items.toTypedArray())
                }
            }
        }
    }
}

@Dao
interface CustomRaffleDao {
    @Transaction
    @Query("select * from $CUSTOM_RAFFLE_TABLE_NAME where $CUSTOM_RAFFLE_ID_COLUMN_NAME = :id ")
    fun getCustomRaffleById(id: Long): CustomRaffleWithItemsDto

    @Transaction
    @Query("select * from $CUSTOM_RAFFLE_TABLE_NAME")
    fun getAllCustomRaffles(): List<CustomRaffleWithItemsDto>

    @Insert(onConflict = REPLACE)
    fun saveCustomRaffle(customRaffleDto: CustomRaffleDto)

    @Query("delete from $CUSTOM_RAFFLE_TABLE_NAME where $CUSTOM_RAFFLE_ID_COLUMN_NAME = :id")
    fun deleteCustomRaffleById(id: Long)
}

@Dao
interface CustomRaffleItemDao {
    @Query("select * from $CUSTOM_RAFFLE_ITEM_TABLE_NAME where $CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME = :id")
    fun getAllCustomRaffleItemsByCustomRaffleId(id: Long): List<CustomRaffleItemDto>

    @Query("delete from $CUSTOM_RAFFLE_ITEM_TABLE_NAME where $CUSTOM_RAFFLE_ITEM_CUSTOM_RAFFLE_ID_COLUMN_NAME = :id")
    fun deleteCustomRaffleItemsByCustomRaffleId(id: Long)

    @Query("delete from $CUSTOM_RAFFLE_ITEM_TABLE_NAME where $CUSTOM_RAFFLE_ITEM_ID_COLUMN_NAME in(:ids)")
    fun deleteCustomRaffleItemsById(ids: List<Long>)

    @Insert(onConflict = REPLACE)
    fun saveCustomRaffleItems(vararg items: CustomRaffleItemDto)
}
