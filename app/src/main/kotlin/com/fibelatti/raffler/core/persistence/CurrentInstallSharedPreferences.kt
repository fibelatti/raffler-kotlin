package com.fibelatti.raffler.core.persistence

import android.content.SharedPreferences
import com.fibelatti.raffler.core.extension.get
import com.fibelatti.raffler.core.extension.put
import com.fibelatti.raffler.core.platform.AppConfig
import javax.inject.Inject

const val KEY_APP_THEME = "APP_THEME"
const val KEY_APP_LANGUAGE = "APP_LANGUAGE"

class CurrentInstallSharedPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun getTheme(): AppConfig.AppTheme {
        return if (sharedPreferences.get<String>(KEY_APP_THEME) == AppConfig.AppTheme.DARK.value) {
            AppConfig.AppTheme.DARK
        } else {
            AppConfig.AppTheme.CLASSIC
        }
    }

    fun setAppTheme(appTheme: AppConfig.AppTheme) {
        sharedPreferences.put(KEY_APP_THEME, appTheme.value)
    }

    fun getAppLanguage(): AppConfig.AppLanguage {
        return when (sharedPreferences.get<String>(KEY_APP_LANGUAGE)) {
            AppConfig.AppLanguage.PORTUGUESE.value -> AppConfig.AppLanguage.PORTUGUESE
            AppConfig.AppLanguage.SPANISH.value -> AppConfig.AppLanguage.SPANISH
            else -> AppConfig.AppLanguage.ENGLISH
        }
    }

    fun setAppLanguage(appLanguage: AppConfig.AppLanguage) {
        sharedPreferences.put(KEY_APP_LANGUAGE, appLanguage.value)
    }
}
