package com.fibelatti.raffler.features.preferences

import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.platform.AppConfig

interface PreferencesRepository {
    suspend fun getPreferences(): Result<Preferences>

    suspend fun setAppTheme(appTheme: AppConfig.AppTheme)

    suspend fun setLanguage(appLanguage: AppConfig.AppLanguage)

    suspend fun setRouletteMusicEnabled(value: Boolean): Result<Unit>

    suspend fun setPreferredRaffleMode(raffleMode: AppConfig.RaffleMode): Result<Unit>

    suspend fun setLotteryDefault(quantityAvailable: Int, quantityToRaffle: Int): Result<Unit>

    suspend fun rememberRaffledItems(value: Boolean): Result<Unit>

    suspend fun resetHints(): Result<Unit>

    suspend fun getQuickDecisionHintDisplayed(): Boolean

    suspend fun setQuickDecisionHintDisplayed(): Result<Unit>
}
