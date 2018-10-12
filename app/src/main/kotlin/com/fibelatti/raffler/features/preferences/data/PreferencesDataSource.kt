package com.fibelatti.raffler.features.preferences.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.fibelatti.raffler.core.extension.getOrDefaultValue
import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.getOrDefault
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.core.persistence.CurrentInstallSharedPreferences
import com.fibelatti.raffler.core.persistence.database.AppDatabase
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.features.preferences.Preferences
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_QUICK_DECISION_HINT = "QUICK_DECISION_HINT"

@Singleton
class PreferencesDataSource @Inject constructor(
    private val currentInstallSharedPreferences: CurrentInstallSharedPreferences,
    private val preferencesDao: PreferencesDao,
    private val appDatabase: AppDatabase,
    private val preferencesDtoMapper: PreferencesDtoMapper
) : PreferencesRepository {

    override suspend fun getPreferences(): Result<Preferences> {
        return runCatching {
            preferencesDao.getPreferences().first().let {
                preferencesDtoMapper.map(it).copy(
                    appTheme = currentInstallSharedPreferences.getTheme(),
                    appLanguage = currentInstallSharedPreferences.getAppLanguage()
                )
            }
        }
    }

    override suspend fun setAppTheme(appTheme: AppConfig.AppTheme) {
        currentInstallSharedPreferences.setAppTheme(appTheme)
    }

    override suspend fun setLanguage(appLanguage: AppConfig.AppLanguage) {
        currentInstallSharedPreferences.setAppLanguage(appLanguage)
    }

    override suspend fun setRouletteMusicEnabled(value: Boolean): Result<Unit> =
        updateCurrentPreferences { it.copy(rouletteMusicEnabled = value) }

    override suspend fun setPreferredRaffleMode(raffleMode: AppConfig.RaffleMode): Result<Unit> =
        updateCurrentPreferences { it.copy(preferredRaffleMode = raffleMode.value) }

    override suspend fun setLotteryDefault(quantityAvailable: Int, quantityToRaffle: Int): Result<Unit> =
        updateCurrentPreferences {
            it.copy(
                lotteryDefaultQuantityAvailable = quantityAvailable,
                lotteryDefaultQuantityToRaffle = quantityToRaffle
            )
        }

    override suspend fun rememberRaffledItems(value: Boolean): Result<Unit> =
        updateCurrentPreferences { it.copy(rememberRaffledItems = value) }

    override suspend fun resetHints(): Result<Unit> = updateCurrentPreferences { it.apply { hintsDisplayed.clear() } }

    override suspend fun getQuickDecisionHintDisplayed(): Boolean =
        runCatching {
            preferencesDao.getPreferences().first()
                .hintsDisplayed.getOrDefaultValue(KEY_QUICK_DECISION_HINT, false)
        }.getOrDefault(false)

    override suspend fun setQuickDecisionHintDisplayed(): Result<Unit> =
        updateCurrentPreferences { it.apply { hintsDisplayed[KEY_QUICK_DECISION_HINT] = true } }

    private fun updateCurrentPreferences(block: (PreferencesDto) -> PreferencesDto): Result<Unit> {
        return preferencesDao.runCatching {
            appDatabase.runInTransaction { updatePreferences(block(getPreferences().first())) }
        }
    }
}

@Dao
interface PreferencesDao {
    @Query("select * from $PREFERENCES_DTO_TABLE_NAME")
    fun getPreferences(): List<PreferencesDto>

    @Update(onConflict = REPLACE)
    fun updatePreferences(preferencesDto: PreferencesDto)
}
