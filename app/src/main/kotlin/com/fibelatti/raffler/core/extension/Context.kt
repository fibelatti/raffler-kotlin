package com.fibelatti.raffler.core.extension

import android.content.Context
import android.content.res.Configuration
import com.fibelatti.raffler.core.persistence.KEY_APP_LANGUAGE
import com.fibelatti.raffler.core.platform.AppConfig
import java.util.Locale

fun Context.getUpdateContextForLocale(): Context {
    val locale = when (getUserPreferences().get<String>(KEY_APP_LANGUAGE)) {
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
