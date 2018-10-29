package com.fibelatti.raffler.core.platform

import com.fibelatti.raffler.core.extension.shouldBe
import org.junit.jupiter.api.Test

class AppConfigTest {

    @Test
    fun testAppConfigValues() {
        AppConfig.PLAY_STORE_BASE_URL shouldBe "https://play.google.com/store/apps/details?id="
        AppConfig.MARKET_BASE_URL shouldBe "market://details?id="

        AppConfig.LOCALE_NONE shouldBe "none"

        AppConfig.AppTheme.CLASSIC.value shouldBe "classic"
        AppConfig.AppTheme.DARK.value shouldBe "dark"

        AppConfig.AppLanguage.ENGLISH.value shouldBe "en"
        AppConfig.AppLanguage.PORTUGUESE.value shouldBe "pt"
        AppConfig.AppLanguage.SPANISH.value shouldBe "es"

        AppConfig.RaffleMode.NONE.value shouldBe "none"
        AppConfig.RaffleMode.ROULETTE.value shouldBe "roulette"
        AppConfig.RaffleMode.RANDOM_WINNERS.value shouldBe "random_winners"
        AppConfig.RaffleMode.GROUPING.value shouldBe "grouping"
        AppConfig.RaffleMode.COMBINATION.value shouldBe "combination"
        AppConfig.RaffleMode.SECRET_VOTING.value shouldBe "secret_voting"
    }
}
