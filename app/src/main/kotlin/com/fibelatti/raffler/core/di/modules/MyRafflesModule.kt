package com.fibelatti.raffler.core.di.modules

import androidx.lifecycle.ViewModel
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelKey
import com.fibelatti.raffler.features.myraffles.CustomRaffleRepository
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleDataSource
import com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle.CreateCustomRaffleViewModel
import com.fibelatti.raffler.features.myraffles.presentation.customraffledetails.CustomRaffleDetailsViewModel
import com.fibelatti.raffler.features.myraffles.presentation.grouping.CustomRaffleGroupingViewModel
import com.fibelatti.raffler.features.myraffles.presentation.list.MyRafflesViewModel
import com.fibelatti.raffler.features.myraffles.presentation.randomwinners.CustomRaffleRandomWinnersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MyRafflesModule {
    @Binds
    abstract fun bindCustomRaffleRepository(
        customRaffleDataSource: CustomRaffleDataSource
    ): CustomRaffleRepository

    @Binds
    @IntoMap
    @ViewModelKey(MyRafflesViewModel::class)
    abstract fun bindMyRafflesViewModel(myRafflesViewModel: MyRafflesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateCustomRaffleViewModel::class)
    abstract fun bindCreateCustomRaffleViewModel(createCustomRaffleViewModel: CreateCustomRaffleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CustomRaffleDetailsViewModel::class)
    abstract fun bindCustomRaffleDetailsViewModel(customRaffleDetailsViewModel: CustomRaffleDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CustomRaffleRandomWinnersViewModel::class)
    abstract fun bindCustomRaffleRandomWinnersViewModel(customRaffleRandomWinnersViewModel: CustomRaffleRandomWinnersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CustomRaffleGroupingViewModel::class)
    abstract fun bindCustomRaffleGroupingViewModel(customRaffleGroupingViewModel: CustomRaffleGroupingViewModel): ViewModel
}
