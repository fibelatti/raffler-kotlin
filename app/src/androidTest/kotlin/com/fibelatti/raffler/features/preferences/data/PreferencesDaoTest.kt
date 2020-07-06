package com.fibelatti.raffler.features.preferences.data

import androidx.test.runner.AndroidJUnit4
import com.fibelatti.core.test.extension.shouldBe
import com.fibelatti.core.test.extension.shouldBeEmpty
import com.fibelatti.raffler.core.persistence.database.BaseDbTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesDaoTest : BaseDbTest() {

    private val preferencesDao by lazy { appDatabase.getPreferencesDao() }

    @Test
    fun whenGetPreferencesIsCalledAndNoPreferencesIsFoundThenEmptyListIsReturned() {
        preferencesDao.getPreferences().shouldBeEmpty()
    }

    @Test
    fun whenGetPreferencesIsCalledThenListIsReturned() {
        // GIVEN
        val updatedPreferences = PreferencesDto(
            id = 1,
            lotteryDefaultQuantityAvailable = 0,
            lotteryDefaultQuantityToRaffle = 0,
            preferredRaffleMode = "",
            rouletteMusicEnabled = false,
            rememberRaffledItems = false,
            hintsDisplayed = mutableMapOf()
        )

        setupInitialData()

        // THEN
        preferencesDao.getPreferences() shouldBe listOf(updatedPreferences)
    }

    @Test
    fun whenSetPreferencesIsCalledThenPreferencesAreUpdated() {
        // GIVEN
        val updatedPreferences = PreferencesDto(
            id = 1,
            lotteryDefaultQuantityAvailable = 0,
            lotteryDefaultQuantityToRaffle = 0,
            preferredRaffleMode = "",
            rouletteMusicEnabled = true,
            rememberRaffledItems = false,
            hintsDisplayed = mutableMapOf()
        )

        setupInitialData()

        // WHEN
        preferencesDao.setPreferences(updatedPreferences)

        // THEN
        preferencesDao.getPreferences() shouldBe listOf(updatedPreferences)
    }

    private fun setupInitialData() {
        preferencesDao.setPreferences(
            PreferencesDto(
                id = 0,
                lotteryDefaultQuantityAvailable = 0,
                lotteryDefaultQuantityToRaffle = 0,
                preferredRaffleMode = "",
                rouletteMusicEnabled = false,
                rememberRaffledItems = false,
                hintsDisplayed = mutableMapOf()
            )
        )
    }
}
