package com.fibelatti.raffler.features.preferences.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val rouletteMusicEnabled by lazy { MutableLiveData<Boolean>() }
    val appTheme by lazy { MutableLiveData<PreferencesRepository.AppTheme>() }

    fun getPreferences() {
        launch {
            rouletteMusicEnabled.value = inBackground {
                preferencesRepository.getRouletteMusicEnabled()
            }
            appTheme.value = inBackground {
                preferencesRepository.getTheme()
            }
        }
    }

    fun setRouletteMusicEnabled(value: Boolean) {
        launch {
            inBackground {
                preferencesRepository.setRouletteMusicEnabled(value)
            }
        }
    }

    fun setAppTheme(appTheme: PreferencesRepository.AppTheme) {
        launch {
            inBackground {
                preferencesRepository.setAppTheme(appTheme)
            }
        }
    }

    fun resetAllHints() {
        launch {
            inBackground {
                preferencesRepository.resetHints()
            }
        }
    }
}
