package com.fibelatti.raffler.features.preferences

interface PreferencesRepository {
    suspend fun getRouletteMusicEnabled(): Boolean

    suspend fun setRouletteMusicEnabled(value: Boolean)

    suspend fun getTheme(): AppTheme

    suspend fun setAppTheme(appTheme: AppTheme)

    suspend fun resetHints()

    suspend fun getQuickDecisionHintDisplayed(): Boolean

    suspend fun setQuickDecisionHintDisplayed()

    enum class AppTheme(val value: String) {
        CLASSIC("classic"), DARK("dark")
    }
}
