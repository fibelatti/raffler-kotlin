package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.App
import com.fibelatti.raffler.core.di.modules.CoreModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    CoreModule::class
])
@Singleton
interface AppComponent {
    fun inject(application: App)
}
