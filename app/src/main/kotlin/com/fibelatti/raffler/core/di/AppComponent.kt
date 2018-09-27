package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.App
import com.fibelatti.raffler.core.di.modules.CoreModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    CoreModule::class
])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: App): Builder
    }

    fun inject(application: App)
}
