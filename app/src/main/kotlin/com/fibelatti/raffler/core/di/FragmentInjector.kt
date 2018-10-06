package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.lottery.presentation.LotteryFragment
import com.fibelatti.raffler.features.myraffles.presentation.CreateCustomRaffleFragment
import com.fibelatti.raffler.features.myraffles.presentation.CustomRaffleDetailsFragment
import com.fibelatti.raffler.features.myraffles.presentation.CustomRaffleRandomWinnersFragment
import com.fibelatti.raffler.features.myraffles.presentation.CustomRaffleRouletteFragment
import com.fibelatti.raffler.features.myraffles.presentation.MyRafflesFragment
import com.fibelatti.raffler.features.preferences.presentation.PreferencesFragment
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionFragment

interface FragmentInjector {
    fun inject(quickDecisionFragment: QuickDecisionFragment)

    fun inject(lotteryFragment: LotteryFragment)

    fun inject(myRafflesFragment: MyRafflesFragment)

    fun inject(customRaffleDetailsFragment: CustomRaffleDetailsFragment)

    fun inject(createCustomRaffleFragment: CreateCustomRaffleFragment)

    fun inject(customRaffleRouletteFragment: CustomRaffleRouletteFragment)

    fun inject(customRaffleRandomWinnersFragment: CustomRaffleRandomWinnersFragment)

    fun inject(preferencesFragment: PreferencesFragment)
}
