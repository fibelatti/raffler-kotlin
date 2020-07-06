package com.fibelatti.raffler.features.preferences.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.extension.gone
import com.fibelatti.core.extension.shareText
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.AppConfig.MAIN_PACKAGE_NAME
import com.fibelatti.raffler.core.platform.AppConfig.PLAY_STORE_BASE_URL
import com.fibelatti.raffler.core.platform.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_preferences.*
import javax.inject.Inject

class PreferencesFragment @Inject constructor() : BaseFragment(R.layout.fragment_preferences) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        layoutSectionGeneral.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentPreferences_to_fragmentPreferencesGeneral)
        }
        layoutSectionLottery.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentPreferences_to_fragmentPreferencesLottery)
        }
        layoutSectionMyRaffles.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentPreferences_to_fragmentPreferencesCustomRaffle)
        }

        setupShareAndRate()

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            textViewAppVersion.text = getString(R.string.preferences_app_version, pInfo.versionName)
        } catch (ignored: Exception) {
            textViewAppVersion.gone()
        }
    }

    private fun setupShareAndRate() {
        buttonShare.setOnClickListener {
            requireActivity().shareText(
                R.string.preferences_share_title,
                getString(
                    R.string.preferences_share_text,
                    "$PLAY_STORE_BASE_URL$MAIN_PACKAGE_NAME"
                )
            )
        }

        buttonRate.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("$PLAY_STORE_BASE_URL${MAIN_PACKAGE_NAME}")
                setPackage("com.android.vending")
            }.let(::startActivity)
        }
    }
}
