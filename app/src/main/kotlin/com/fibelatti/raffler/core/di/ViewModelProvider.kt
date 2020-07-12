package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.lottery.presentation.LotteryViewModel
import com.fibelatti.raffler.features.myraffles.presentation.combination.CustomRaffleCombinationViewModel
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleViewModel
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsViewModel
import com.fibelatti.raffler.features.myraffles.presentation.grouping.CustomRaffleGroupingViewModel
import com.fibelatti.raffler.features.myraffles.presentation.list.MyRafflesViewModel
import com.fibelatti.raffler.features.myraffles.presentation.randomwinners.CustomRaffleRandomWinnersViewModel
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingViewModel
import com.fibelatti.raffler.features.preferences.presentation.PreferencesViewModel
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionResultViewModel
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionViewModel
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.AddNewQuickDecisionViewModel

interface ViewModelProvider {

    fun lotteryViewModel(): LotteryViewModel

    fun myRafflesViewModel(): MyRafflesViewModel
    fun createCustomRaffleViewModel(): CreateCustomRaffleViewModel
    fun customRaffleDetailsViewModel(): CustomRaffleDetailsViewModel
    fun customRaffleRandomWinnersViewModel(): CustomRaffleRandomWinnersViewModel
    fun customRaffleGroupingViewModel(): CustomRaffleGroupingViewModel
    fun customRaffleCombinationViewModel(): CustomRaffleCombinationViewModel
    fun customRaffleVotingViewModel(): CustomRaffleVotingViewModel

    fun quickDecisionViewModel(): QuickDecisionViewModel
    fun quickDecisionResultViewModel(): QuickDecisionResultViewModel
    fun addNewQuickDecisionViewModel(): AddNewQuickDecisionViewModel

    fun preferencesViewModel(): PreferencesViewModel
}
