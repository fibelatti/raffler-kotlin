package com.fibelatti.raffler.core.di.modules.viewmodel

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
