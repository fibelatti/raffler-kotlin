package com.fibelatti.raffler.core.di.modules

import androidx.lifecycle.ViewModel
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelKey
import com.fibelatti.raffler.features.lottery.presentation.LotteryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LotteryModule {
    @Binds
    @IntoMap
    @ViewModelKey(LotteryViewModel::class)
    abstract fun bindLotteryViewModel(lotteryViewModel: LotteryViewModel): ViewModel
}
