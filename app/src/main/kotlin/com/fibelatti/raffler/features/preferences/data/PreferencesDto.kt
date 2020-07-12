package com.fibelatti.raffler.features.preferences.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fibelatti.core.functional.Mapper
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.features.preferences.Preferences
import javax.inject.Inject

const val PREFERENCES_DTO_TABLE_NAME = "Preferences"
const val PREFERENCES_TABLE_INITIAL_SETUP = "INSERT INTO `Preferences` VALUES (1, 0, 0, '', 0, 0, '')"

@Keep
@Entity(tableName = PREFERENCES_DTO_TABLE_NAME)
data class PreferencesDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val lotteryDefaultQuantityAvailable: Int,
    val lotteryDefaultQuantityToRaffle: Int,
    val preferredRaffleMode: String,
    val rouletteMusicEnabled: Boolean,
    val rememberRaffledItems: Boolean,
    val hintsDisplayed: Map<String, Boolean>
)

class PreferencesDtoMapper @Inject constructor() : Mapper<PreferencesDto, Preferences> {

    override fun map(param: PreferencesDto): Preferences = with(param) {
        Preferences(
            id = id,
            appTheme = AppConfig.AppTheme.CLASSIC,
            appLanguage = AppConfig.AppLanguage.ENGLISH,
            rouletteMusicEnabled = rouletteMusicEnabled,
            preferredRaffleMode = when (preferredRaffleMode) {
                AppConfig.RaffleMode.ROULETTE.value -> AppConfig.RaffleMode.ROULETTE
                AppConfig.RaffleMode.RANDOM_WINNERS.value -> AppConfig.RaffleMode.RANDOM_WINNERS
                AppConfig.RaffleMode.GROUPING.value -> AppConfig.RaffleMode.GROUPING
                AppConfig.RaffleMode.COMBINATION.value -> AppConfig.RaffleMode.COMBINATION
                AppConfig.RaffleMode.SECRET_VOTING.value -> AppConfig.RaffleMode.SECRET_VOTING
                else -> AppConfig.RaffleMode.NONE
            },
            lotteryDefaultQuantityAvailable = lotteryDefaultQuantityAvailable.takeIf { it != 0 }?.toString().orEmpty(),
            lotteryDefaultQuantityToRaffle = lotteryDefaultQuantityToRaffle.takeIf { it != 0 }?.toString().orEmpty(),
            rememberRaffledItems = rememberRaffledItems,
            hintsDisplayed = hintsDisplayed
        )
    }
}
