package com.fibelatti.raffler.features.preferences

import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.platform.AppConfig

interface PreferencesRepository {
    suspend fun getTheme(): AppConfig.AppTheme

    suspend fun setAppTheme(appTheme: AppConfig.AppTheme)

    suspend fun getLanguage(): AppConfig.AppLanguage

    suspend fun setLanguage(appLanguage: AppConfig.AppLanguage)

    suspend fun getRouletteMusicEnabled(): Boolean

    suspend fun setRouletteMusicEnabled(value: Boolean): Result<Unit>

    suspend fun resetHints(): Result<Unit>

    suspend fun getQuickDecisionHintDisplayed(): Boolean

    suspend fun setQuickDecisionHintDisplayed()
}
