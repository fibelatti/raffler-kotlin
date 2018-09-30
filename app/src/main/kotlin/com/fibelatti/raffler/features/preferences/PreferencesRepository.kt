package com.fibelatti.raffler.features.preferences

import com.fibelatti.raffler.core.functional.Either
import com.fibelatti.raffler.core.platform.AppConfig

interface PreferencesRepository {
    suspend fun getTheme(): AppConfig.AppTheme

    suspend fun setAppTheme(appTheme: AppConfig.AppTheme)

    suspend fun getRouletteMusicEnabled(): Boolean

    suspend fun setRouletteMusicEnabled(value: Boolean): Either<Throwable, Unit>

    suspend fun resetHints(): Either<Throwable, Unit>

    suspend fun getQuickDecisionHintDisplayed(): Boolean

    suspend fun setQuickDecisionHintDisplayed()
}
