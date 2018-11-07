package com.fibelatti.raffler

import com.fibelatti.raffler.core.extension.empty
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.features.myraffles.CustomRaffle
import com.fibelatti.raffler.features.myraffles.CustomRaffleItem
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import com.fibelatti.raffler.features.preferences.Preferences

object MockDataProvider {
    const val genericString = "Some string"
    const val defaultId = 1L

    fun mockPreferences(
        appTheme: AppConfig.AppTheme = AppConfig.AppTheme.CLASSIC,
        appLanguage: AppConfig.AppLanguage = AppConfig.AppLanguage.ENGLISH,
        lotteryDefaultQuantityAvailable: String = String.empty(),
        lotteryDefaultQuantityToRaffle: String = String.empty(),
        preferredRaffleMode: AppConfig.RaffleMode = AppConfig.RaffleMode.NONE,
        rouletteMusicEnabled: Boolean = false,
        rememberRaffledItems: Boolean = false,
        hintsDisplayed: MutableMap<String, Boolean> = mutableMapOf()
    ) = Preferences(
        id = defaultId,
        appTheme = appTheme,
        appLanguage = appLanguage,
        lotteryDefaultQuantityAvailable = lotteryDefaultQuantityAvailable,
        lotteryDefaultQuantityToRaffle = lotteryDefaultQuantityToRaffle,
        preferredRaffleMode = preferredRaffleMode,
        rouletteMusicEnabled = rouletteMusicEnabled,
        rememberRaffledItems = rememberRaffledItems,
        hintsDisplayed = hintsDisplayed
    )

    fun mockCustomRaffle(
        id: Long = defaultId,
        description: String = String.empty(),
        items: List<CustomRaffleItem> = emptyList()
    ) = CustomRaffle(id, description, items)

    fun mockCustomRaffleItem(
        id: Long = defaultId,
        customRaffleId: Long = defaultId,
        description: String = String.empty(),
        included: Boolean = true
    ) = CustomRaffleItem(id, customRaffleId, description, included)

    fun mockCustomRaffleModel(
        id: Long = defaultId,
        description: String = String.empty(),
        items: MutableList<CustomRaffleItemModel> = mutableListOf()
    ) = CustomRaffleModel(id, description, items)

    fun mockCustomRaffleItemModel(
        id: Long = defaultId,
        customRaffleId: Long = defaultId,
        description: String = String.empty(),
        included: Boolean = true
    ) = CustomRaffleItemModel(id, customRaffleId, description, included)
}
