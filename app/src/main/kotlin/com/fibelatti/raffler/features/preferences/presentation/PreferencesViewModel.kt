package com.fibelatti.raffler.features.preferences.presentation

import androidx.lifecycle.MutableLiveData
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.BaseViewModel
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.fibelatti.raffler.core.provider.ThreadProvider
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val resourceProvider: ResourceProvider,
    threadProvider: ThreadProvider
) : BaseViewModel(threadProvider) {

    val appTheme by lazy { MutableLiveData<AppConfig.AppTheme>() }
    val rouletteMusicEnabled by lazy { MutableLiveData<Boolean>() }
    val updateFeedback by lazy { MutableLiveData<String>() }

    fun getPreferences() {
        start {
            rouletteMusicEnabled.value = inBackground {
                preferencesRepository.getRouletteMusicEnabled()
            }
            appTheme.value = inBackground {
                preferencesRepository.getTheme()
            }
        }
    }

    fun setRouletteMusicEnabled(value: Boolean) {
        start {
            inBackground {
                preferencesRepository.setRouletteMusicEnabled(value)
            }.either(
                { error.value = it },
                { updateFeedback.value = resourceProvider.getString(R.string.preferences_changes_saved) }
            )
        }
    }

    fun setAppTheme(appTheme: AppConfig.AppTheme) {
        start {
            inBackground {
                preferencesRepository.setAppTheme(appTheme)
            }
        }
    }

    fun resetAllHints() {
        start {
            inBackground {
                preferencesRepository.resetHints()
            }.either(
                { error.value = it },
                { updateFeedback.value = resourceProvider.getString(R.string.preferences_changes_saved) }
            )
        }
    }
}
