package com.fibelatti.raffler.features.preferences

import com.fibelatti.raffler.core.platform.AppConfig

data class Preferences(
    private val id: Long,
    val appTheme: AppConfig.AppTheme,
    val appLanguage: AppConfig.AppLanguage,
    val lotteryDefaultQuantityAvailable: String,
    val lotteryDefaultQuantityToRaffle: String,
    val preferredRaffleMode: AppConfig.RaffleMode,
    val rouletteMusicEnabled: Boolean,
    val rememberRaffledItems: Boolean,
    val hintsDisplayed: Map<String, Boolean>
)
