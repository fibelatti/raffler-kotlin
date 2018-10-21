package com.fibelatti.raffler.core.di.modules

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.fibelatti.raffler.App
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelFactory
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

@Module(includes = [
    CoreModule.Binder::class,
    PersistenceModule::class
])
object CoreModule {
    @Module
    interface Binder {
        @Binds
        fun bindContext(app: App): Context

        @Binds
        fun bindResourceProvider(appResourceProvider: AppResourceProvider): ResourceProvider

        @Binds
        fun bindCoroutineLauncher(coroutineLauncherDelegate: CoroutineLauncherDelegate): CoroutineLauncher

        @Binds
        fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
    }

    @Provides
    @JvmStatic
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @JvmStatic
    fun provideLocaleDefault(): Locale = Locale.getDefault()

    @Provides
    @JvmStatic
    fun provideUSCollator(): Collator = Collator.getInstance(Locale.US)
}
