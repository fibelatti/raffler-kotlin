package com.fibelatti.raffler.features.preferences.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.remove
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.AppConfig.MARKET_BASE_URL
import com.fibelatti.raffler.core.platform.AppConfig.PLAY_STORE_BASE_URL
import com.fibelatti.raffler.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences.*

class PreferencesFragment : BaseFragment() {

    private val preferencesViewModel by lazy {
        viewModelFactory.get<PreferencesViewModel>(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        preferencesViewModel.run {
            error(error, ::handleError)
            observe(appTheme, ::setupTheme)
            observe(rouletteMusicEnabled, ::setupRouletteMusicEnabled)
            observe(updateFeedback) { layoutRoot.snackbar(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preferences, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupListeners()
        preferencesViewModel.getPreferences()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.title_preferences)
        layoutTitle.navigateUp { layoutRoot.findNavController().navigateUp() }

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            textViewAppVersion.text = getString(R.string.preferences_app_version, pInfo.versionName)
        } catch (e: Exception) {
            textViewAppVersion.gone()
        }
    }

    private fun setupListeners() {
        buttonResetHints.setOnClickListener { preferencesViewModel.resetAllHints() }
        setupShareAndRate()
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

    private fun setupRouletteMusicEnabled(value: Boolean) {
        checkBoxRouletteMusic.apply {
            setOnCheckedChangeListener(null)
            isChecked = value
            setOnCheckedChangeListener { _, isChecked ->
                preferencesViewModel.setRouletteMusicEnabled(isChecked)
            }
        }
    }

    private fun setupShareAndRate() {
        val appName = requireContext().packageName.remove(".debug")

        buttonShare.setOnClickListener {
            val message = getString(R.string.preferences_share_text, "$PLAY_STORE_BASE_URL$appName")
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            startActivity(Intent.createChooser(share, getString(R.string.preferences_share_title)))
        }

        buttonRate.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$MARKET_BASE_URL$appName")))
            } catch (exception: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$PLAY_STORE_BASE_URL$appName")))
            }
        }
    }
}
