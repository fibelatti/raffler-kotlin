package com.fibelatti.raffler.features.preferences

interface PreferencesRepository {
    suspend fun getRouletteMusicEnabled(): Boolean

    suspend fun setRouletteMusicEnabled(value: Boolean)

    suspend fun resetHints()

    suspend fun getQuickDecisionHintDisplayed(): Boolean

    suspend fun setQuickDecisionHintDisplayed()
}
