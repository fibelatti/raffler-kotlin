package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.lottery.presentation.LotteryFragment
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsFragment
import com.fibelatti.raffler.features.myraffles.presentation.grouping.CustomRaffleGroupingFragment
import com.fibelatti.raffler.features.myraffles.presentation.list.MyRafflesFragment
import com.fibelatti.raffler.features.myraffles.presentation.randomwinners.CustomRaffleRandomWinnersFragment
import com.fibelatti.raffler.features.myraffles.presentation.roulette.CustomRaffleRouletteFragment
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionFragment

interface FragmentInjector {
    fun inject(quickDecisionFragment: QuickDecisionFragment)

    fun inject(lotteryFragment: LotteryFragment)

    fun inject(myRafflesFragment: MyRafflesFragment)

    fun inject(customRaffleDetailsFragment: CustomRaffleDetailsFragment)

    fun inject(createCustomRaffleFragment: CreateCustomRaffleFragment)

    fun inject(customRaffleRouletteFragment: CustomRaffleRouletteFragment)

    fun inject(customRaffleRandomWinnersFragment: CustomRaffleRandomWinnersFragment)

    fun inject(customRaffleGroupingFragment: CustomRaffleGroupingFragment)
}
