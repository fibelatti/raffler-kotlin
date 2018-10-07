package com.fibelatti.raffler.features.preferences.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.fibelatti.raffler.core.extension.getOrDefaultValue
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.core.persistence.CurrentInstallSharedPreferences
import com.fibelatti.raffler.core.persistence.database.AppDatabase
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

private const val KEY_QUICK_DECISION_HINT = "QUICK_DECISION_HINT"

class PreferencesDataSource @Inject constructor(
    private val currentInstallSharedPreferences: CurrentInstallSharedPreferences,
    private val appDatabase: AppDatabase,
    private val preferencesDao: PreferencesDao
) : PreferencesRepository {

    override suspend fun getTheme(): AppConfig.AppTheme =
        currentInstallSharedPreferences.getTheme()

    override suspend fun setAppTheme(appTheme: AppConfig.AppTheme) {
        currentInstallSharedPreferences.setAppTheme(appTheme)
    }

    override suspend fun getLanguage(): AppConfig.AppLanguage =
        currentInstallSharedPreferences.getAppLanguage()

    override suspend fun setLanguage(appLanguage: AppConfig.AppLanguage) {
        currentInstallSharedPreferences.setAppLanguage(appLanguage)
    }

    override suspend fun getRouletteMusicEnabled(): Boolean =
        preferencesDao.getPreferences().firstOrNull()?.rouletteMusicEnabled.orFalse()

    override suspend fun setRouletteMusicEnabled(value: Boolean): Result<Unit> {
        return runCatching { preferencesDao.setRouletteMusicEnabled(value) }
    }

    override suspend fun resetHints(): Result<Unit> {
        return runCatching {
            appDatabase.runInTransaction {
                val updatedPreferences = preferencesDao.getPreferences().first().let {
                    it.copy(hintsDisplayed = it.hintsDisplayed.mapValues { false }.toMutableMap())
                }

                preferencesDao.updatePreferences(updatedPreferences)
            }
        }
    }

    override suspend fun getQuickDecisionHintDisplayed(): Boolean =
        preferencesDao.getPreferences().firstOrNull()
            ?.hintsDisplayed?.getOrDefaultValue(KEY_QUICK_DECISION_HINT, false).orFalse()

    override suspend fun setQuickDecisionHintDisplayed() {
        val updatedPreferences = preferencesDao.getPreferences().first().apply {
            hintsDisplayed[KEY_QUICK_DECISION_HINT] = true
        }

        preferencesDao.updatePreferences(updatedPreferences)
    }
}

@Dao
interface PreferencesDao {
    @Query("select * from $PREFERENCES_DTO_TABLE_NAME")
    fun getPreferences(): List<PreferencesDto>

    @Update(onConflict = REPLACE)
    fun updatePreferences(preferencesDto: PreferencesDto)

    @Query("update $PREFERENCES_DTO_TABLE_NAME set $PREFERENCES_DTO_ROULETTE_MUSIC_ENABLED_COLUMN_NAME = :value")
    fun setRouletteMusicEnabled(value: Boolean)
}
