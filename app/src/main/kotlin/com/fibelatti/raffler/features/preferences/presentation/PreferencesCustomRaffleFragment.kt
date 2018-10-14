package com.fibelatti.raffler.features.preferences.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.platform.AppConfig
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModes
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModesDelegate
import kotlinx.android.synthetic.main.fragment_preferences_custom_raffle.*

class PreferencesCustomRaffleFragment :
    BaseFragment(),
    CustomRaffleModes by CustomRaffleModesDelegate() {

    private val preferencesViewModel by lazy {
        viewModelFactory.get<PreferencesViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesViewModel.run {
            error(error, ::handleError)
            observe(preferences) {
                setupRouletteMusicEnabled(it.rouletteMusicEnabled)
                setupPreferredRaffleMode(it.preferredRaffleMode)
                setupRememberRaffledItems(it.rememberRaffledItems)
            }
            observeEvent(updateFeedback) { layoutRoot.snackbar(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preferences_custom_raffle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        preferencesViewModel.getPreferences()
    }

    private fun setupLayout() {
        layoutTitle.setTitle(R.string.preferences_section_my_raffles)
        layoutTitle.navigateUp { layoutRoot.findNavController().navigateUp() }
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
