package com.fibelatti.raffler

import android.app.Application
import com.fibelatti.raffler.core.di.AppComponent
import com.fibelatti.raffler.core.di.DaggerAppComponent

class App : Application() {
    companion object {
        val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
            DaggerAppComponent
                .builder()
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        this.injectMembers()
    }

    private fun injectMembers() = appComponent.inject(this)
}
