package com.fibelatti.raffler.core.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.fibelatti.raffler.core.di.mapkeys.FragmentKey
import com.fibelatti.raffler.core.extension.getUserPreferences
import com.fibelatti.raffler.features.lottery.presentation.LotteryFragment
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleDataSource
import com.fibelatti.raffler.features.myraffles.presentation.combination.CustomRaffleCombinationFragment
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleFragment
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsFragment
import com.fibelatti.raffler.features.myraffles.presentation.grouping.CustomRaffleGroupingFragment
import com.fibelatti.raffler.features.myraffles.presentation.list.MyRafflesFragment
import com.fibelatti.raffler.features.myraffles.presentation.randomwinners.CustomRaffleRandomWinnersFragment
import com.fibelatti.raffler.features.myraffles.presentation.roulette.CustomRaffleRouletteFragment
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingMenuFragment
import com.fibelatti.raffler.features.myraffles.presentation.voting.CustomRaffleVotingStartFragment
import com.fibelatti.raffler.features.myraffles.presentation.voting.results.CustomRaffleVotingResultsFragment
import com.fibelatti.raffler.features.myraffles.presentation.voting.vote.CustomRaffleVotingVoteFragment
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import com.fibelatti.raffler.features.preferences.data.PreferencesDataSource
import com.fibelatti.raffler.features.preferences.presentation.PreferencesCustomRaffleFragment
import com.fibelatti.raffler.features.preferences.presentation.PreferencesFragment
import com.fibelatti.raffler.features.preferences.presentation.PreferencesGeneralFragment
import com.fibelatti.raffler.features.preferences.presentation.PreferencesLotteryFragment
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDataSource
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionFragment
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionResultFragment
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.AddNewQuickDecisionFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class FeatureModule {

    companion object {

        @Provides
        fun Context.userSharedPreferences(): SharedPreferences = getUserPreferences()
    }

    @Binds
    abstract fun PreferencesDataSource.preferencesRepository(): PreferencesRepository

    @Binds
    abstract fun CustomRaffleDataSource.customRaffleRepository(): CustomRaffleRepository

    @Binds
    abstract fun QuickDecisionDataSource.quickDecisionRepository(): QuickDecisionRepository

    // region Fragments
    @Binds
    @IntoMap
    @FragmentKey(AddNewQuickDecisionFragment::class)
    abstract fun AddNewQuickDecisionFragment.addNewQuickDecisionFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CreateCustomRaffleFragment::class)
    abstract fun CreateCustomRaffleFragment.createCustomRaffleFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleCombinationFragment::class)
    abstract fun CustomRaffleCombinationFragment.customRaffleCombinationFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleDetailsFragment::class)
    abstract fun CustomRaffleDetailsFragment.customRaffleDetailsFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleGroupingFragment::class)
    abstract fun CustomRaffleGroupingFragment.customRaffleGroupingFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleRandomWinnersFragment::class)
    abstract fun CustomRaffleRandomWinnersFragment.customRaffleRandomWinnersFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleRouletteFragment::class)
    abstract fun CustomRaffleRouletteFragment.customRaffleRouletteFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleVotingMenuFragment::class)
    abstract fun CustomRaffleVotingMenuFragment.customRaffleVotingMenuFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleVotingResultsFragment::class)
    abstract fun CustomRaffleVotingResultsFragment.customRaffleVotingResultsFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleVotingStartFragment::class)
    abstract fun CustomRaffleVotingStartFragment.customRaffleVotingStartFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CustomRaffleVotingVoteFragment::class)
    abstract fun CustomRaffleVotingVoteFragment.customRaffleVotingVoteFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(LotteryFragment::class)
    abstract fun LotteryFragment.lotteryFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(MyRafflesFragment::class)
    abstract fun MyRafflesFragment.myRafflesFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PreferencesFragment::class)
    abstract fun PreferencesFragment.preferencesFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PreferencesCustomRaffleFragment::class)
    abstract fun PreferencesCustomRaffleFragment.preferencesCustomRaffleFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PreferencesGeneralFragment::class)
    abstract fun PreferencesGeneralFragment.preferencesGeneralFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PreferencesLotteryFragment::class)
    abstract fun PreferencesLotteryFragment.preferencesLotteryFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(QuickDecisionFragment::class)
    abstract fun QuickDecisionFragment.quickDecisionFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(QuickDecisionResultFragment::class)
    abstract fun QuickDecisionResultFragment.quickDecisionResultFragment(): Fragment
    // endregion
}
