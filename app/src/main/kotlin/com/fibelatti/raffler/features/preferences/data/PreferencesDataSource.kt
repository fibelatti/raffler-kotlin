package com.fibelatti.raffler.features.preferences.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.fibelatti.raffler.core.extension.orFalse
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

private const val HINT_KEY_QUICK_DECISION = "HINT_KEY_QUICK_DECISION"
private const val HINT_KEY_ADD_NEW_QUICK_DECISION = "HINT_KEY_ADD_NEW_QUICK_DECISION"
private const val HINT_KEY_LOTTERY = "HINT_KEY_LOTTERY"
private const val HINT_KEY_RAFFLE_DETAILS = "HINT_KEY_RAFFLE_DETAILS"

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

    override suspend fun resetHints(): Result<Unit> = updateCurrentPreferences {
        it.copy(hintsDisplayed = mutableMapOf())
    }

    override suspend fun getQuickDecisionHintDisplayed(): Boolean = getHintDisplayed(HINT_KEY_QUICK_DECISION)

    override suspend fun setQuickDecisionHintDismissed(): Result<Unit> = setHintDisplayed(HINT_KEY_QUICK_DECISION)

    override suspend fun getAddNewQuickDecisionDisplayed(): Boolean = getHintDisplayed(HINT_KEY_ADD_NEW_QUICK_DECISION)

    override suspend fun setAddNewQuickDecisionDismissed(): Result<Unit> = setHintDisplayed(HINT_KEY_ADD_NEW_QUICK_DECISION)

    override suspend fun getLotteryHintDisplayed(): Boolean = getHintDisplayed(HINT_KEY_LOTTERY)

    override suspend fun setLotteryHintDismissed(): Result<Unit> = setHintDisplayed(HINT_KEY_LOTTERY)

    override suspend fun getRaffleDetailsHintDisplayed(): Boolean = getHintDisplayed(HINT_KEY_RAFFLE_DETAILS)

    override suspend fun setRaffleDetailsHintDismissed(): Result<Unit> = setHintDisplayed(HINT_KEY_RAFFLE_DETAILS)

    private fun getHintDisplayed(key: String): Boolean =
        runCatching { preferencesDao.getPreferences().first().hintsDisplayed[key].orFalse() }
            .getOrDefault(false)

    private fun setHintDisplayed(key: String): Result<Unit> =
        updateCurrentPreferences {
            it.copy(hintsDisplayed = it.hintsDisplayed.toMutableMap().apply { set(key, true) })
        }

    private fun updateCurrentPreferences(block: (PreferencesDto) -> PreferencesDto): Result<Unit> {
        return preferencesDao.runCatching {
            appDatabase.runInTransaction {
                val currentPreferences = getPreferences().first()
                val updatedPreferences = block(currentPreferences)
                updatePreferences(updatedPreferences)
            }
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
