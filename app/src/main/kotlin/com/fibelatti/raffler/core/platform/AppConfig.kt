package com.fibelatti.raffler.core.platform

object AppConfig {
    const val PLAY_STORE_BASE_URL = "https://play.google.com/store/apps/details?id="
    const val MARKET_BASE_URL = "market://details?id="

    const val LOCALE_NONE = "none"
    const val LOCALE_EN = "en"
    const val LOCALE_PT = "pt"
    const val LOCALE_ES = "es"

    @JvmStatic
    val supportedLocales: List<String> by lazy { listOf(LOCALE_EN, LOCALE_PT, LOCALE_ES) }

    enum class AppTheme(val value: String) {
        CLASSIC("classic"), DARK("dark")
    }
}
