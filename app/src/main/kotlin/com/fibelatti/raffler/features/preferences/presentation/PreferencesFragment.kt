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
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.remove
import com.fibelatti.raffler.core.platform.AppConfig.MARKET_BASE_URL
import com.fibelatti.raffler.core.platform.AppConfig.PLAY_STORE_BASE_URL
import com.fibelatti.raffler.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences.*

class PreferencesFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preferences, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        layoutSectionGeneral.setOnClickListener {
            layoutRoot.findNavController().navigate(R.id.action_fragmentPreferences_to_fragmentPreferencesGeneral)
        }
        layoutSectionLottery.setOnClickListener {
            layoutRoot.findNavController().navigate(R.id.action_fragmentPreferences_to_fragmentPreferencesLottery)
        }
        layoutSectionMyRaffles.setOnClickListener {
            layoutRoot.findNavController().navigate(R.id.action_fragmentPreferences_to_fragmentPreferencesCustomRaffle)
        }

        setupShareAndRate()

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            textViewAppVersion.text = getString(R.string.preferences_app_version, pInfo.versionName)
        } catch (e: Exception) {
            textViewAppVersion.gone()
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
