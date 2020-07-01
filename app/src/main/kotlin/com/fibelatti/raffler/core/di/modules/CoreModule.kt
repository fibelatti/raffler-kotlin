package com.fibelatti.raffler.core.di.modules

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentFactory
import com.fibelatti.core.android.MultiBindingFragmentFactory
import com.fibelatti.raffler.core.platform.AppResourceProvider
import com.fibelatti.raffler.core.provider.CoroutineLauncher
import com.fibelatti.raffler.core.provider.CoroutineLauncherDelegate
import com.fibelatti.raffler.core.provider.ResourceProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import java.text.Collator
import java.util.Locale

@Module
abstract class CoreModule {

    companion object {

        @Provides
        fun gson(): Gson = GsonBuilder().create()

        @Provides
        fun localeDefault(): Locale = Locale.getDefault()

        @Provides
        fun collatorUs(): Collator = Collator.getInstance(Locale.US)
    }

    @Binds
    abstract fun bindContext(app: Application): Context

    @Binds
    abstract fun AppResourceProvider.resourceProvider(): ResourceProvider

    @Binds
    abstract fun bindCoroutineLauncher(coroutineLauncherDelegate: CoroutineLauncherDelegate): CoroutineLauncher

    @Binds
    abstract fun MultiBindingFragmentFactory.fragmentFactory(): FragmentFactory
}
