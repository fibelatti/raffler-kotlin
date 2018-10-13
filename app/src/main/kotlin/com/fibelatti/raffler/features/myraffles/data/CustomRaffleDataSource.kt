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
import com.fibelatti.raffler.features.myraffles.CustomRaffleItem
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import javax.inject.Inject

class CustomRaffleDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val customRaffleDao: CustomRaffleDao,
    private val customRaffleItemDao: CustomRaffleItemDao,
    private val customRaffleWithItemsDtoMapper: CustomRaffleWithItemsDtoMapper,
    private val customRaffleItemDtoMapper: CustomRaffleItemDtoMapper
) : CustomRaffleRepository {
    override suspend fun getAllCustomRaffles(): Result<List<CustomRaffle>> =
        customRaffleDao.runCatching { getAllCustomRaffles().let(customRaffleWithItemsDtoMapper::mapList) }

    override suspend fun getCustomRaffleById(id: Long): Result<CustomRaffle> =
        customRaffleDao.runCatching { getCustomRaffleById(id).let(customRaffleWithItemsDtoMapper::map) }

    override suspend fun saveCustomRaffle(customRaffle: CustomRaffle): Result<Unit> {
        return runCatching {
            with(customRaffle.let(customRaffleWithItemsDtoMapper::mapReverse)) {
                appDatabase.runInTransaction {
                    customRaffleDao.deleteCustomRaffleById(customRaffleDto.id)
                    val customRaffleId = customRaffleDao.saveCustomRaffle(customRaffleDto)
                    val items = items.map { it.copy(customRaffleId = customRaffleId) }
                    customRaffleItemDao.saveCustomRaffleItems(*items.toTypedArray())
                }
            }
        }
    }

    override suspend fun updateCustomRaffleItem(customRaffleItem: CustomRaffleItem): Result<Unit> =
        customRaffleItemDao.runCatching {
            saveCustomRaffleItems(customRaffleItemDtoMapper.mapReverse(customRaffleItem))
        }

    override suspend fun deleteCustomRaffleById(id: Long): Result<Unit> =
        customRaffleDao.runCatching { deleteCustomRaffleById(id) }
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
    fun saveCustomRaffle(customRaffleDto: CustomRaffleDto): Long

    @Query("delete from $CUSTOM_RAFFLE_TABLE_NAME where $CUSTOM_RAFFLE_ID_COLUMN_NAME = :id")
    fun deleteCustomRaffleById(id: Long)
}

@Dao
interface CustomRaffleItemDao {
    @Insert(onConflict = REPLACE)
    fun saveCustomRaffleItems(vararg items: CustomRaffleItemDto)
}
