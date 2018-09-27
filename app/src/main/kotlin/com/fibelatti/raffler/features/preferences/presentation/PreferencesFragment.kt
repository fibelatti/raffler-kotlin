package com.fibelatti.raffler.features.preferences.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelFactory
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.remove
import com.fibelatti.raffler.core.platform.AppConfig.MARKET_BASE_URL
import com.fibelatti.raffler.core.platform.AppConfig.PLAY_STORE_BASE_URL
import com.fibelatti.raffler.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences.*
import javax.inject.Inject

class PreferencesFragment : BaseFragment() {

    companion object {
        val TAG: String = PreferencesFragment::class.java.simpleName

        fun newInstance() = PreferencesFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val preferencesViewModel by lazy {
        viewModelFactory.of<PreferencesViewModel>(this@PreferencesFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector?.inject(this)
        preferencesViewModel.run {
            observe(rouletteMusicEnabled) { checkBoxRouletteMusic.isChecked = it }

            getPreferences()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preferences, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupListeners()
    }

    private fun setupLayout() {
        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            textViewAppVersion.text = getString(R.string.preferences_app_version, pInfo.versionName)
        } catch (e: Exception) {
            textViewAppVersion.gone()
        }
    }

    private fun setupListeners() {
        checkBoxRouletteMusic.setOnCheckedChangeListener { _, isChecked ->
            preferencesViewModel.setRouletteMusicEnabled(isChecked)
        }

        buttonResetHints.setOnClickListener { preferencesViewModel.resetAllHints() }

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
