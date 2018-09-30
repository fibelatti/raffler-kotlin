package com.fibelatti.raffler.core.persistence

import android.content.SharedPreferences
import com.fibelatti.raffler.core.extension.get
import com.fibelatti.raffler.core.extension.put
import com.fibelatti.raffler.core.platform.AppConfig
import javax.inject.Inject

const val KEY_APP_THEME = "APP_THEME"

class CurrentInstallSharedPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun getTheme(): AppConfig.AppTheme {
        return if (sharedPreferences.get<String>(KEY_APP_THEME) == AppConfig.AppTheme.CLASSIC.value) {
            AppConfig.AppTheme.CLASSIC
        } else {
            AppConfig.AppTheme.DARK
        }
    }

    fun setAppTheme(appTheme: AppConfig.AppTheme) {
        sharedPreferences.put(KEY_APP_THEME, appTheme.value)
    }
}
