package com.fibelatti.raffler.features.preferences.presentation

import android.arch.lifecycle.MutableLiveData
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

    fun getPreferences() {
        launch {
            rouletteMusicEnabled.value = inBackground {
                preferencesRepository.getRouletteMusicEnabled()
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

    fun resetAllHints() {
        launch {
            inBackground {
                preferencesRepository.resetHints()
            }
        }
    }
}
