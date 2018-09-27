package com.fibelatti.raffler

import android.app.Application
import com.fibelatti.raffler.core.di.AppComponent
import com.fibelatti.raffler.core.di.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }
}
