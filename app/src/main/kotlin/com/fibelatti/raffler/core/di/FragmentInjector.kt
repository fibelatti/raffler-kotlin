package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.lottery.presentation.LotteryFragment
import com.fibelatti.raffler.features.myraffles.presentation.MyRafflesFragment
import com.fibelatti.raffler.features.preferences.presentation.PreferencesFragment
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionFragment

interface FragmentInjector {
    fun inject(quickDecisionFragment: QuickDecisionFragment)

    fun inject(lotteryFragment: LotteryFragment)

    fun inject(myRafflesFragment: MyRafflesFragment)

    fun inject(preferencesFragment: PreferencesFragment)
}
