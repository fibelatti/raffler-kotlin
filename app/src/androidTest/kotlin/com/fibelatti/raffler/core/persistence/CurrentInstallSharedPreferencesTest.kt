package com.fibelatti.raffler.core.persistence

import android.content.SharedPreferences
import com.fibelatti.raffler.BaseTest
import com.fibelatti.raffler.MockSharedPreferencesEditor
import com.fibelatti.raffler.core.extension.get
import com.fibelatti.raffler.core.extension.mock
import com.fibelatti.raffler.core.extension.shouldBe
import com.fibelatti.raffler.core.platform.AppConfig
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class CurrentInstallSharedPreferencesTest : BaseTest() {
    private val mockSharedPreferences = mock<SharedPreferences>()
    private val mockEditor = spy(MockSharedPreferencesEditor())

    private val currentInstallSharedPreferences = CurrentInstallSharedPreferences(
        mockSharedPreferences
    )

    @Before
    fun setup() {
        given(mockSharedPreferences.edit())
            .willReturn(mockEditor)
    }

    @Test
    fun whenGetThemeIsCalledAndValueIsClassicThenCLASSICisReturned() {
        // GIVEN
        given(mockSharedPreferences.get<String>(KEY_APP_THEME))
            .willReturn("classic")

        // WHEN
        val result = currentInstallSharedPreferences.getTheme()

        // THEN
        result shouldBe AppConfig.AppTheme.CLASSIC
    }

    @Test
    fun whenGetThemeIsCalledAndValueIsDarkThenDARKisReturned() {
        // GIVEN
        given(mockSharedPreferences.get<String>(KEY_APP_THEME))
            .willReturn("dark")

        // WHEN
        val result = currentInstallSharedPreferences.getTheme()

        // THEN
        result shouldBe AppConfig.AppTheme.DARK
    }

    @Test
    fun whenGetThemeIsCalledAndValueIsNullThenCLASSICisReturned() {
        // GIVEN
        given(mockSharedPreferences.get<String>(KEY_APP_THEME))
            .willReturn(null)

        // WHEN
        val result = currentInstallSharedPreferences.getTheme()

        // THEN
        result shouldBe AppConfig.AppTheme.CLASSIC
    }

    @Test
    fun whenSetAppThemeIsCalledThenKEY_APP_THEMEisWrittenToSharedPreferences() {
        // WHEN
        currentInstallSharedPreferences.setAppTheme(AppConfig.AppTheme.CLASSIC)

        // THEN
        verify(mockEditor).putString(KEY_APP_THEME, AppConfig.AppTheme.CLASSIC.value)
    }

    @Test
    fun whenGetAppLanguageIsCalledAndValueIsPtThenPORTUGUESEisReturned() {
        // GIVEN
        given(mockSharedPreferences.get<String>(KEY_APP_LANGUAGE))
            .willReturn("pt")

        // WHEN
        val result = currentInstallSharedPreferences.getAppLanguage()

        // THEN
        result shouldBe AppConfig.AppLanguage.PORTUGUESE
    }

    @Test
    fun whenGetAppLanguageIsCalledAndValueIsEsThenSPANISHisReturned() {
        // GIVEN
        given(mockSharedPreferences.get<String>(KEY_APP_LANGUAGE))
            .willReturn("es")

        // WHEN
        val result = currentInstallSharedPreferences.getAppLanguage()

        // THEN
        result shouldBe AppConfig.AppLanguage.SPANISH
    }

    @Test
    fun whenGetAppLanguageIsCalledThenENGLISHisReturned() {
        // GIVEN
        given(mockSharedPreferences.get<String>(KEY_APP_LANGUAGE))
            .willReturn(null)

        // WHEN
        val result = currentInstallSharedPreferences.getAppLanguage()

        // THEN
        result shouldBe AppConfig.AppLanguage.ENGLISH
    }

    @Test
    fun whenSetAppLanguageIsCalledThenKEY_APP_LANGUAGEisWrittenToSharedPreferences() {
        // WHEN
        currentInstallSharedPreferences.setAppLanguage(AppConfig.AppLanguage.ENGLISH)

        // THEN
        verify(mockEditor).putString(KEY_APP_LANGUAGE, AppConfig.AppLanguage.ENGLISH.value)
    }
}
