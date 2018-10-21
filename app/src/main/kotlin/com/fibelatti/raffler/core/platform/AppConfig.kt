package com.fibelatti.raffler.core.platform

object AppConfig {
    const val PLAY_STORE_BASE_URL = "https://play.google.com/store/apps/details?id="
    const val MARKET_BASE_URL = "market://details?id="

    const val LOCALE_NONE = "none"

    enum class AppTheme(val value: String) {
        CLASSIC("classic"), DARK("dark")
    }

    enum class AppLanguage(val value: String) {
        ENGLISH("en"), PORTUGUESE("pt"), SPANISH("es")
    }

    enum class RaffleMode(val value: String) {
        NONE("none"),
        ROULETTE("roulette"),
        RANDOM_WINNERS("random_winners"),
        GROUPING("grouping"),
        COMBINATION("combination"),
        SECRET_VOTING("secret_voting")
    }
}
