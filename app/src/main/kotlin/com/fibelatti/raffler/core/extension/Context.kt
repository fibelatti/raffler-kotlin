package com.fibelatti.raffler.core.extension

import android.content.Context
import android.content.res.Configuration
import com.fibelatti.core.extension.get
import com.fibelatti.core.extension.getSharedPreferences
import com.fibelatti.raffler.core.persistence.KEY_APP_LANGUAGE
import com.fibelatti.raffler.core.platform.AppConfig
import java.util.Locale

fun Context.getUserPreferences() = getSharedPreferences("user_preferences")

fun Context.getUpdateContextForLocale(): Context {
    val locale = when (getUserPreferences().get(KEY_APP_LANGUAGE, Locale.getDefault().language)) {
        AppConfig.AppLanguage.PORTUGUESE.value -> Locale("pt", "BR")
        AppConfig.AppLanguage.SPANISH.value -> Locale("es", "ES")
        else -> Locale("en", "US")
    }

    Locale.setDefault(locale)

    val config = Configuration(resources.configuration).apply {
        setLocale(locale)
    }

    return createConfigurationContext(config)
}
