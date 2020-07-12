package com.fibelatti.raffler.features.preferences.presentation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.snackbar
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences_general.*
import javax.inject.Inject

class PreferencesGeneralFragment @Inject constructor() : BaseFragment(R.layout.fragment_preferences_general) {

    private val preferencesViewModel by viewModel { viewModelProvider.preferencesViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()

        viewLifecycleOwner.observe(preferencesViewModel.error, ::handleError)
        viewLifecycleOwner.observe(preferencesViewModel.preferences) {
            setupTheme(it.appTheme)
            setupLanguage(it.appLanguage)
        }
        viewLifecycleOwner.observeEvent(preferencesViewModel.updateFeedback) {
            layoutRoot.snackbar(
                message = it,
                background = R.drawable.background_snackbar,
                textColor = R.color.text_primary
            )
        }

        preferencesViewModel.getPreferences()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.preferences_section_general)
        layoutTitle.setNavigateUp { findNavController().navigateUp() }
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

            activity?.recreate()
        }
    }
}
