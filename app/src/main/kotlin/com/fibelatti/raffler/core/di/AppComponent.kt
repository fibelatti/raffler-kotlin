package com.fibelatti.raffler.core.di

import android.app.Application
import androidx.fragment.app.FragmentFactory
import com.fibelatti.raffler.core.di.modules.CoreModule
import com.fibelatti.raffler.core.di.modules.DatabaseModule
import com.fibelatti.raffler.core.di.modules.FeatureModule
import com.fibelatti.raffler.core.persistence.CurrentInstallSharedPreferences
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        CoreModule::class,
        DatabaseModule::class,
        FeatureModule::class
    ]
)
@Singleton
interface AppComponent : ViewModelProvider {

    fun fragmentFactory(): FragmentFactory
    fun currentInstallSharedPreferences(): CurrentInstallSharedPreferences

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }
}

interface AppComponentProvider {
    val appComponent: AppComponent
}
