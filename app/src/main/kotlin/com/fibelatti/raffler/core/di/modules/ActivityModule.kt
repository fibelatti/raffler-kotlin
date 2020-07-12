package com.fibelatti.raffler.core.di.modules

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentFactory
import com.fibelatti.core.android.MultiBindingFragmentFactory
import com.fibelatti.raffler.core.di.ActivityContext
import com.fibelatti.raffler.core.platform.AppResourceProvider
import com.fibelatti.raffler.core.provider.ResourceProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ActivityModule {

    @ActivityContext
    @Binds
    abstract fun bindContext(activity: Activity): Context

    @Binds
    abstract fun MultiBindingFragmentFactory.fragmentFactory(): FragmentFactory

    @Binds
    abstract fun AppResourceProvider.resourceProvider(): ResourceProvider
}
