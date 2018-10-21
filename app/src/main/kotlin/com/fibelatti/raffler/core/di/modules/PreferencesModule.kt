package com.fibelatti.raffler.core.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelKey
import com.fibelatti.raffler.core.extension.getUserPreferences
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import com.fibelatti.raffler.features.preferences.data.PreferencesDataSource
import com.fibelatti.raffler.features.preferences.presentation.PreferencesViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class PreferencesModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideSharedPreferences(context: Context): SharedPreferences =
            context.getUserPreferences()
    }

    @Binds
    abstract fun bindPreferencesRepository(
        preferencesDataSource: PreferencesDataSource
    ): PreferencesRepository

    @Binds
    @IntoMap
    @ViewModelKey(PreferencesViewModel::class)
    abstract fun bindPreferencesViewModel(preferencesViewModel: PreferencesViewModel): ViewModel
}
