package com.fibelatti.raffler.features.preferences.presentation

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences_general.*
import javax.inject.Inject

private const val RESTART_DELAY = 1000L

class PreferencesGeneralFragment @Inject constructor() : BaseFragment() {

    private val preferencesViewModel by activityViewModel { viewModelProvider.preferencesViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesViewModel.run {
            error(error, ::handleError)
            observe(preferences) {
                setupTheme(it.appTheme)
                setupLanguage(it.appLanguage)
            }
            observeEvent(updateFeedback) { layoutRoot.snackbar(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preferences_general, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        preferencesViewModel.getPreferences()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.preferences_section_general)
        layoutTitle.setNavigateUp { layoutRoot.findNavController().navigateUp() }
        buttonResetHints.setOnClickListener { preferencesViewModel.resetAllHints() }
    }

    private fun setupTheme(appTheme: AppConfig.AppTheme) {
        radioGroupTheme.setOnCheckedChangeListener(null)

        if (appTheme == AppConfig.AppTheme.CLASSIC) {
            radioButtonThemeClassic.isChecked = true
        } else {
            radioButtonThemeDark.isChecked = true
        }

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonThemeClassic -> {
                    preferencesViewModel.setAppTheme(AppConfig.AppTheme.CLASSIC)
                }
                R.id.radioButtonThemeDark -> {
                    preferencesViewModel.setAppTheme(AppConfig.AppTheme.DARK)
                }
            }

            activity?.recreate()
        }
    }

    private fun setupLanguage(appLanguage: AppConfig.AppLanguage) {
        radioGroupLanguage.setOnCheckedChangeListener(null)

        when (appLanguage) {
            AppConfig.AppLanguage.PORTUGUESE -> radioButtonLanguagePortuguese.isChecked = true
            AppConfig.AppLanguage.SPANISH -> radioButtonLanguageSpanish.isChecked = true
            else -> radioButtonLanguageEnglish.isChecked = true
        }

        radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonLanguageEnglish -> {
                    preferencesViewModel.setAppLanguage(AppConfig.AppLanguage.ENGLISH)
                }
                R.id.radioButtonLanguagePortuguese -> {
                    preferencesViewModel.setAppLanguage(AppConfig.AppLanguage.PORTUGUESE)
                }
                R.id.radioButtonLanguageSpanish -> {
                    preferencesViewModel.setAppLanguage(AppConfig.AppLanguage.SPANISH)
                }
            }

            Handler().postDelayed({ activity?.recreate() }, RESTART_DELAY)
        }
    }
}
