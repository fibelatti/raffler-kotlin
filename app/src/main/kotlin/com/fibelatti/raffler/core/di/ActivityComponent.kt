package com.fibelatti.raffler.core.di

import android.app.Activity
import androidx.fragment.app.FragmentFactory
import com.fibelatti.raffler.core.di.modules.ActivityModule
import com.fibelatti.raffler.core.di.modules.FeatureModule
import com.fibelatti.raffler.features.InAppUpdateManager
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(
    modules = [
        ActivityModule::class,
        FeatureModule::class
    ]
)
interface ActivityComponent : ViewModelProvider {

    fun fragmentFactory(): FragmentFactory
    fun inAppUpdateManager(): InAppUpdateManager

    @Subcomponent.Factory
    interface Factory {

        fun create(@BindsInstance activity: Activity): ActivityComponent
    }
}


