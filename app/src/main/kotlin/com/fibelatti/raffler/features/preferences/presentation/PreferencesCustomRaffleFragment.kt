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
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModes
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModesDelegate
import kotlinx.android.synthetic.main.fragment_preferences_custom_raffle.*
import javax.inject.Inject

class PreferencesCustomRaffleFragment @Inject constructor() : BaseFragment(
    R.layout.fragment_preferences_custom_raffle
), CustomRaffleModes by CustomRaffleModesDelegate() {

    private val preferencesViewModel by viewModel { viewModelProvider.preferencesViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()

        viewLifecycleOwner.observe(preferencesViewModel.error, ::handleError)
        viewLifecycleOwner.observe(preferencesViewModel.preferences) {
            setupRouletteMusicEnabled(it.rouletteMusicEnabled)
            setupPreferredRaffleMode(it.preferredRaffleMode)
            setupRememberRaffledItems(it.rememberRaffledItems)
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
        layoutTitle.setTitle(R.string.preferences_section_my_raffles)
        layoutTitle.setNavigateUp { findNavController().navigateUp() }
    }

    private fun setupPreferredRaffleMode(raffleMode: AppConfig.RaffleMode) {
        textViewPreferredRaffleModeMessage.text = getString(
            when (raffleMode) {
                AppConfig.RaffleMode.ROULETTE -> R.string.custom_raffle_details_mode_roulette
                AppConfig.RaffleMode.RANDOM_WINNERS -> R.string.custom_raffle_details_mode_random_winners
                AppConfig.RaffleMode.GROUPING -> R.string.custom_raffle_details_mode_grouping
                AppConfig.RaffleMode.COMBINATION -> R.string.custom_raffle_details_mode_combination
                AppConfig.RaffleMode.SECRET_VOTING -> R.string.custom_raffle_details_mode_secret_voting
                AppConfig.RaffleMode.NONE -> R.string.custom_raffle_details_mode_none
            }
        )

        buttonPreferredRaffleMode.setOnClickListener {
            showRaffleModes(
                requireContext(),
                rouletteClickListener = {
                    preferencesViewModel.setPreferredRaffleMode(AppConfig.RaffleMode.ROULETTE)
                },
                randomWinnersClickListener = {
                    preferencesViewModel.setPreferredRaffleMode(AppConfig.RaffleMode.RANDOM_WINNERS)
                },
                groupingClickListener = {
                    preferencesViewModel.setPreferredRaffleMode(AppConfig.RaffleMode.GROUPING)
                },
                combinationClickListener = {
                    preferencesViewModel.setPreferredRaffleMode(AppConfig.RaffleMode.COMBINATION)
                },
                votingClickListener = {
                    preferencesViewModel.setPreferredRaffleMode(AppConfig.RaffleMode.SECRET_VOTING)
                }
            )
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

    private fun setupRememberRaffledItems(value: Boolean) {
        checkboxRememberRaffled.apply {
            setOnCheckedChangeListener(null)
            isChecked = value
            setOnCheckedChangeListener { _, isChecked ->
                preferencesViewModel.setRememberRaffledItems(isChecked)
            }
        }
    }
}
