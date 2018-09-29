package com.fibelatti.raffler.features.preferences.data

import android.content.SharedPreferences
import com.fibelatti.raffler.core.extension.get
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.extension.put
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

const val KEY_ROULETTE_MUSIC_ENABLED = "ROULETTE_MUSIC_ENABLED"
const val KEY_APP_THEME = "APP_THEME"
const val KEY_QUICK_DECISION_HINT_DISPLAYED = "QUICK_DECISION_HINT_DISPLAYED"

class PreferencesDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferencesRepository {
    override suspend fun getRouletteMusicEnabled(): Boolean =
        sharedPreferences.get(KEY_ROULETTE_MUSIC_ENABLED, false).orFalse()

    override suspend fun setRouletteMusicEnabled(value: Boolean) {
        sharedPreferences.put(KEY_ROULETTE_MUSIC_ENABLED, value)
    }

    override suspend fun getTheme(): PreferencesRepository.AppTheme {
        return if (sharedPreferences.get<String>(KEY_APP_THEME) == PreferencesRepository.AppTheme.CLASSIC.value) {
            PreferencesRepository.AppTheme.CLASSIC
        } else {
            PreferencesRepository.AppTheme.DARK
        }
    }

    override suspend fun setAppTheme(appTheme: PreferencesRepository.AppTheme) {
        sharedPreferences.put(KEY_APP_THEME, appTheme.value)
    }

    override suspend fun resetHints() {
        sharedPreferences.put(KEY_QUICK_DECISION_HINT_DISPLAYED, false)
    }

    override suspend fun getQuickDecisionHintDisplayed(): Boolean =
        sharedPreferences.get(KEY_QUICK_DECISION_HINT_DISPLAYED, false).orFalse()

    override suspend fun setQuickDecisionHintDisplayed() {
        sharedPreferences.put(KEY_QUICK_DECISION_HINT_DISPLAYED, true)
    }
}

fun SharedPreferences.getDarkModeEnabled(): Boolean =
    get<String>(KEY_APP_THEME).equals(PreferencesRepository.AppTheme.DARK.value)
